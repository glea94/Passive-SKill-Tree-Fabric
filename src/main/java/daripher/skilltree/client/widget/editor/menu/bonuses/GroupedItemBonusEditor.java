package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionList;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionMenu;
import daripher.skilltree.client.widget.editor.menu.selection.TextSelectionList;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.item.GroupedItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class GroupedItemBonusEditor {
    private GroupedItemBonusEditor() {}

    public static void addEditorWidgets(GroupedItemBonus groupedBonus, SkillTreeEditor editor, Consumer<GroupedItemBonus> consumer) {
        ItemBonus<?> defaultBonus = PSTItemBonuses.SKILL_BONUS.get().createDefaultInstance();
        editor.addSelectionMenu(0, 0, 90, defaultBonus).setResponder(itemBonus -> {
            addItemBonus(groupedBonus, editor, itemBonus);
            consumer.accept(groupedBonus);
        }).setMessage(Component.literal("Add"));
        editor.increaseHeight(29);
        for (int i = 0; i < groupedBonus.getInnerBonuses().size(); i++) {
            final int bonusIndex = i;
            ItemBonus selectedItemBonus = groupedBonus.getInnerBonuses().get(i);
            MutableComponent tooltip = groupedBonus.getFullTooltip().get(0);
            String message = tooltip.getString();
            message = TooltipHelper.getTrimmedString(message, 190);
            editor.addButton(0, 0, 200, 14, message).setPressFunc(button -> {
                ItemBonusEditor itemBonusEditor = new ItemBonusEditor(editor, editor.getSelectedMenu(), bonus -> skillBonusChanged(groupedBonus, bonus, bonusIndex, consumer), () -> selectedItemBonus);
                editor.selectMenu(itemBonusEditor);
            });
            editor.increaseHeight(19);
        }
    }

    private static void skillBonusChanged(GroupedItemBonus groupedBonus, @Nullable ItemBonus<?> itemBonus, int selectedBonusIndex, Consumer<GroupedItemBonus> consumer) {
        if (itemBonus == null) {
            groupedBonus.removeInnerBonus(selectedBonusIndex);
        } else {
            groupedBonus.setInnerBonus(selectedBonusIndex, itemBonus);
        }
        consumer.accept(groupedBonus.copy());
    }

    private static void addItemBonus(GroupedItemBonus groupedBonus, SkillTreeEditor editor, ItemBonus<?> itemBonus) {
        final EditorMenu previousMenu = editor.getSelectedMenu().previousMenu;
        if (itemBonus instanceof EquipmentBonus equipmentBonus) {
            SelectionList<SkillBonus> skillBonusSelectionList = new TextSelectionList<>(0, 0, 190, 14, PSTSkillBonuses.defaultInstances()).setRows(8)
                    .setNameGetter(bonus -> Component.literal(PSTSkillBonuses.getName(bonus)))
                    .selectElement(equipmentBonus.getSkillBonus());
            editor.selectMenu(new SelectionMenu<>(editor, editor.getSelectedMenu(), skillBonusSelectionList, () -> {
            }).setResponder(skillBonus -> {
                groupedBonus.addInnerBonus(new EquipmentBonus(skillBonus));
                editor.selectMenu(previousMenu);
            }));
            return;
        }
        groupedBonus.addInnerBonus(itemBonus);
        editor.selectMenu(previousMenu);
    }
}