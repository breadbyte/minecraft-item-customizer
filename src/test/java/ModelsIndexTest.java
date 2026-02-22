import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModelsIndexTest {
    static final String NAMESPACE = "test";

    @Test
    public void testAddAndGet() {
        ModelsIndex index = ModelsIndex.testHarness();
        var nsc = new NamespaceCategory(NAMESPACE, "category");
        CustomModelDefinition model = new CustomModelDefinition(nsc, "name", "creator");
        index.add(model);
        assertEquals(model, index.get(nsc, "name"));
    }

    @Test
    public void testGetNonExistent() {
        ModelsIndex index = ModelsIndex.testHarness();
        CustomModelDefinition model = index.get(new NamespaceCategory("nonexistent", "nonexistent"), "name");
        assertNull(model);
    }

    @Test
    public void testAddDuplicate() {
        ModelsIndex index = ModelsIndex.testHarness();
        var nsc = new NamespaceCategory(NAMESPACE, "category");
        CustomModelDefinition model = new CustomModelDefinition(nsc,"name", "creator");
        index.add(model);
        index.add(model);
        assertEquals(model, index.get(nsc, "name"));
        assertEquals(1, index.getAllShallow(nsc).size());
    }

    @Test
    public void testAddCategoryNested() {
        ModelsIndex index = ModelsIndex.testHarness();
        final String CATEGORY = "category";
        var nsc1 = new NamespaceCategory(NAMESPACE, CATEGORY).appendCategory("nest1");
        var nsc2 = nsc1.appendCategory("nest2");

        // Create models
        // (Test for both trailing slashes)

        // NamespaceCategory is the preferred way to create models
        // (since it does formatting on the input)
        var model = new CustomModelDefinition(nsc1, "name", "creator");
        var model3 = new CustomModelDefinition(nsc2, "name3", "creator");

        // We still support direct string input, so we should still handle these cases
        var model2 = new CustomModelDefinition(NAMESPACE, CATEGORY + "/nest1", "name2", "creator");
        var model4 = new CustomModelDefinition(NAMESPACE, CATEGORY + "/nest1/nest2", "name4", "creator");

        // Add models
        index.add(model);
        index.add(model2);
        index.add(model3);
        index.add(model4);

        var recursive = index.getAllRecursive(nsc1);
        var shallow = index.getAllShallow(nsc1);

        // Ensure all models exist
        assertEquals(model, index.get(nsc1, "name"));
        assertEquals(model2, index.get(nsc1, "name2"));
        assertEquals(model3, index.get(nsc2, "name3"));
        assertEquals(model4, index.get(nsc2, "name4"));

        // Recursive should get all 4 models, shallow should only get the 2 directly in the category
        assertEquals(4, recursive.size());
        assertEquals(2, shallow.size());
    }
}
