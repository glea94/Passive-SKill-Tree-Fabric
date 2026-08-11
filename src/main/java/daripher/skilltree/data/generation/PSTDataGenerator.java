package daripher.skilltree.data.generation;

import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PSTDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // Creates the main asset generation pack container
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // Registers language providers using the mandatory 1.21.4 asynchronous lookup contexts
        pack.addProvider((output, registriesFuture) -> new PSTEnglishTranslationProvider(output, registriesFuture));
        pack.addProvider((output, registriesFuture) -> new PSTRussianTranslationProvider(output, registriesFuture));
    }
}
