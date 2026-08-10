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
}
