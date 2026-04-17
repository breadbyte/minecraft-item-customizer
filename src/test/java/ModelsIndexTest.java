import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModelsIndexTest {
    static final String NAMESPACE = "test";

    @Test
    public void testAddAndGet() {
        ModelsIndex index = ModelsIndex.testHarness();
        var nsc = new ModelPath(NAMESPACE, "category", "name");
        CustomModelDefinition model = new CustomModelDefinition(nsc, "creator");
        index.add(model);
        assertEquals(model, index.get(nsc, "name"));
    }

    @Test
    public void testGetNonExistent() {
        ModelsIndex index = ModelsIndex.testHarness();
        CustomModelDefinition model = index.get(new ModelPath("nonexistent", "nonexistent"), "name");
        assertNull(model);
    }

    @Test
    public void testAddDuplicate() {
        ModelsIndex index = ModelsIndex.testHarness();
        var nsc = new ModelPath(NAMESPACE, "category", "name");
        CustomModelDefinition model = new CustomModelDefinition(nsc,"creator");
        index.add(model);
        index.add(model);
        assertEquals(model, index.get(nsc, "name"));
        assertEquals(1, index.getAllShallow(nsc).size());
    }

    @Test
    public void testAddCategoryNested() {
        ModelsIndex index = ModelsIndex.testHarness();
        final String CATEGORY = "category";
        var nsc1 = new ModelPath(NAMESPACE, CATEGORY, "name").appendCategory("nest1");
        var nsc2 = nsc1.appendCategory("nest2");

        // Create models
        // (Test for both trailing slashes)

        // ModelPath is the preferred way to create models
        // (since it does formatting on the input)
        var model = new CustomModelDefinition(nsc1, "creator");
        var model3 = new CustomModelDefinition(nsc2, "creator");

        // We still support direct string input, so we should still handle these cases
        var model2 = new CustomModelDefinition(nsc1, "creator");
        var model4 = new CustomModelDefinition(nsc2, "creator");

        // Add models
        index.add(model);
        index.add(model2);
        index.add(model3);
        index.add(model4);

        var recursive = index.getAllRecursive(nsc1);
        var shallow = index.getAllShallow(nsc1);

        // Ensure all models exist
        assertEquals(model, index.get(nsc1, nsc1.itemName()));
        assertEquals(model2, index.get(nsc1, "name2"));
        assertEquals(model3, index.get(nsc2, "name3"));
        assertEquals(model4, index.get(nsc2, "name4"));

        // Recursive should get all 4 models, shallow should only get the 2 directly in the category
        assertEquals(4, recursive.size());
        assertEquals(2, shallow.size());
    }

    @Test
    public void testCategoryNestReturns() {
        var nsc1 = new ModelPath(NAMESPACE, "category", "name").appendCategory("nest1");
        var nsc2 = nsc1.appendCategory("nest2");

        assertEquals("category/nest1/nest2", nsc2.getCategory());
        assertEquals("name", nsc2.getItemName());
    }
}
