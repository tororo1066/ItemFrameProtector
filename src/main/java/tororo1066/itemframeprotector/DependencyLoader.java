package tororo1066.itemframeprotector;

import tororo1066.tororopluginapi.AbstractDependencyLoader;
import tororo1066.tororopluginapi.Library;
import tororo1066.tororopluginapi.LibraryType;

public class DependencyLoader extends AbstractDependencyLoader {
    @Override
    public Library[] getDependencies() {
        return new Library[]{
                LibraryType.KOTLIN.createLibrary("2.2.0"),
                LibraryType.MONGODB.createLibrary()
        };
    }
}
