package daripher.skilltree.data.generation;

import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Portage Fabric de PSTDataGenerator. Ne reprend QUE la génération des traductions
 * anglaise/russe (celles manquantes en statique) : tags, recette du workbench, blockstate/
 * modèles et table de butin ont été écrits à la main directement en JSON (portée trop réduite -
 * 1 bloc, 2 items - pour justifier de porter tout le système de datagen de modèles Forge, dont
 * l'API diffère beaucoup côté Fabric).
 * <p>
 * Déclarée comme entrypoint "fabric-datagen" dans fabric.mod.json. Lancer ensuite
 * ./gradlew runDatagen une fois pour produire les fichiers lang/en_us.json et lang/ru_ru.json.
 */
public class PSTDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider((output, registriesFuture) -> new PSTEnglishTranslationProvider(output));
        pack.addProvider((output, registriesFuture) -> new PSTRussianTranslationProvider(output));
    }
}
