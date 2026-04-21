import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.internal.CSVFetcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModelsIndexDepthTest {
    static ModelsIndex INDEX = null;

    static final String URL_ENV_VARIABLE = "NAMESPACE_URL";
    static final String NAMESPACE_VARIABLE = "NAMESPACE_NAME";
    static final String NAMESPACE_CATEGORY = "NAMESPACE_CATEGORY";
    static final String NAMESPACE_SUBCATEGORY = "NAMESPACE_SUBCATEGORY";

    @BeforeAll
    public static void PreSetup() {
        assertNotNull(System.getenv(URL_ENV_VARIABLE), String.format("Environment variable '%s' must be set", URL_ENV_VARIABLE));
        assertNotNull(System.getenv(NAMESPACE_VARIABLE), String.format("Environment variable '%s' must be set", NAMESPACE_VARIABLE));
        assertNotNull(System.getenv(NAMESPACE_CATEGORY), String.format("Environment variable '%s' must be set", NAMESPACE_CATEGORY));
        assertNotNull(System.getenv(NAMESPACE_SUBCATEGORY), String.format("Environment variable '%s' must be set", NAMESPACE_SUBCATEGORY));

        INDEX = ModelsIndex.testHarness();
        INDEX.clear();

        String namespace = System.getenv(NAMESPACE_VARIABLE);
        String url = System.getenv(URL_ENV_VARIABLE);

        try {
            var values = CSVFetcher.fetchAsync(namespace, URI.create(url).toURL()).join();
            INDEX.addAll(values);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void VerifyPopulated() {
        String namespace = System.getenv(NAMESPACE_VARIABLE);

        // Assuming the CSV contains a structure like namespace:category/item
        // This test verifies that we can retrieve entries at specific depths
        ModelPath path = ModelPath.of(namespace + ":");
        List<CustomModelDefinition> entries = INDEX.getEntriesAt(path);

        assertNotNull(entries);
        assertFalse(entries.isEmpty(), "Index should not be empty");
    }

    @Test
    public void GetRoot() {
        String namespace = System.getenv(NAMESPACE_VARIABLE);

        ModelPath path = ModelPath.of(String.format("%s:", namespace));
        var entries = INDEX.__internalAutocomplete(String.valueOf(path));

        System.out.println("Contents:");
        for (var entry : entries) {
            System.out.println(entry);
        }

        assertNotNull(entries);
        assertFalse(entries.isEmpty(), "Index should not be empty");
    }

    @Test
    public void GetNestedRoot() {
        String namespace = System.getenv(NAMESPACE_VARIABLE);
        String category = System.getenv(NAMESPACE_CATEGORY);

        ModelPath path = ModelPath.of(String.format("%s:%s", namespace, category));

        var entries = INDEX.__internalAutocomplete(String.valueOf(path));

        System.out.println("Contents:");
        for (var entry : entries) {
            System.out.println(entry);
        }

        assertNotNull(entries);
        assertFalse(entries.isEmpty(), "Index should not be empty");
    }

    @Test
    public void GetSecondNestedRoot() {
        String namespace = System.getenv(NAMESPACE_VARIABLE);
        String category = System.getenv(NAMESPACE_CATEGORY);
        String subcategory = System.getenv(NAMESPACE_SUBCATEGORY);

        ModelPath path = ModelPath.of(String.format("%s:%s/%s", namespace, category, subcategory));
        var entries = INDEX.__internalAutocomplete(String.valueOf(path));

        System.out.println("Contents:");
        for (var entry : entries) {
            System.out.println(entry);
        }

        assertNotNull(entries);
        assertFalse(entries.isEmpty(), "Index should not be empty");
    }
}
