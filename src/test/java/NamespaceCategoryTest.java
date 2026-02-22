import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NamespaceCategoryTest {

    @Test
    public void appendCategoryAppendsCorrectly() {
        NamespaceCategory category = new NamespaceCategory("test", "category");
        NamespaceCategory result = category.appendCategory("new").appendCategory("new2");
        assertEquals("category/new/new2", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesEmptyCategory() {
        NamespaceCategory category = new NamespaceCategory("test", "");
        NamespaceCategory result = category.appendCategory("new");
        assertEquals("new", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesTrailingSlash() {
        NamespaceCategory category = new NamespaceCategory("test", "category/");
        NamespaceCategory result = category.appendCategory("new");
        assertEquals("category/new", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesMultipleSlashes() {
        NamespaceCategory category = new NamespaceCategory("test", "category//");
        NamespaceCategory result = category.appendCategory("new");
        assertEquals("category/new", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesNullCategory() {
        assertThrows(NullPointerException.class, () -> {
            new NamespaceCategory("test", null);
        });
    }
}
