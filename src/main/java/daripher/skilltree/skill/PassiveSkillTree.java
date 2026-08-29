package daripher.skilltree.skill;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class PassiveSkillTree {
    private final List<Identifier> skillIds = new ArrayList<>();
    private final Identifier id;
    private @Nullable Map<String, Integer> skillLimitations;
    private boolean hidden;
    public PassiveSkillTree(Identifier id) {
        this.id = id;
    }
    public Identifier getId() {
        return id;
    }
    public List<Identifier> getSkillIds() {
        return skillIds;
    }
    public Map<String, Integer> getSkillLimitations() {
        if (skillLimitations == null) {
            return skillLimitations = new LinkedHashMap<>();
        }
        return skillLimitations;
    }
    /**
     * Un arbre "hidden" (ex: skilltree:mace_mastery) n'est jamais listé dans
     * SkillTreeSelectionScreen ni dans SkillTreesReloader.getOrderedSkillTreeIds()
     * (donc pas de flèches prev/next vers/depuis lui). Son seul accès est un bouton
     * dédié ajouté par le code de l'arbre parent (ex: bouton Mace Mastering dans
     * SkillTreeWidgets quand skillTree == skilltree:blacksmith).
     */
    public boolean isHidden() {
        return hidden;
    }
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}