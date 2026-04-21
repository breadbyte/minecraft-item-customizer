import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ModelPathTest {

    @Test
    public void appendCategoryAppendsCorrectly() {
//        ModelPath category = new ModelPath("test", "category");
//        ModelPath result = category.appendCategory("new").appendCategory("new2");
//        assertEquals("category/new/new2", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesEmptyCategory() {
//        ModelPath category = new ModelPath("test", "");
//        ModelPath result = category.appendCategory("new");
//        assertEquals("new", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesTrailingSlash() {
//        ModelPath category = new ModelPath("test", "category/");
//        ModelPath result = category.appendCategory("new");
//        assertEquals("category/new", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesMultipleSlashes() {
//        ModelPath category = new ModelPath("test", "category//");
//        ModelPath result = category.appendCategory("new");
//        assertEquals("category/new", result.getCategory());
    }

    @Test
    public void appendCategoryHandlesNullCategory() {
//        assertThrows(NullPointerException.class, () -> {
//            new ModelPath("test", null);
//        });
    }
}
