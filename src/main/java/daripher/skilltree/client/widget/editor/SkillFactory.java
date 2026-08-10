package daripher.skilltree.client.widget.editor;

import daripher.skilltree.skill.PassiveSkill;
import org.apache.logging.log4j.util.TriConsumer;

@FunctionalInterface
public interface SkillFactory extends TriConsumer<Float, Float, PassiveSkill> {
    // Keeps a clean, type-safe contract for floating-point calculations across Java 21+ environments
}
