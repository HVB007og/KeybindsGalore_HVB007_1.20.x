package net.hvb007.keybindsgalore;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeybindsManager {

    // Creates an Hashmap? IDK what a hashmap is.
    private static final Map<InputUtil.Key, List<KeyBinding>> conflictingKeys = Maps.newHashMap();

    //When conflicting keys are pressed this creates a Array List of all the bindings bound to same key
    public static boolean handleConflict(InputUtil.Key key) {
        List<KeyBinding> matches = new ArrayList<>();
        KeyBinding[] keysAll = MinecraftClient.getInstance().options.allKeys;
        for (KeyBinding bind: keysAll) {
            if (bind.matchesKey(key.getCode(), -1)) {
                matches.add(bind);
            }
        }
        if (matches.size() > 1) {
            KeybindsManager.conflictingKeys.put(key, matches);
            return true;
        } else {
            KeybindsManager.conflictingKeys.remove(key);
            return false;
        }
    }

    //boolean returning if there are multiple bindings bound or not to the same key
    public static boolean isConflicting(InputUtil.Key key) {
        return conflictingKeys.containsKey(key);
    }

    //Initializes and opens the Circle selector thingy
    public static void openConflictMenu(InputUtil.Key key) {
        KeybindsScreen screen = new KeybindsScreen();
        screen.setConflictedKey(key);
        MinecraftClient.getInstance().setScreen(screen);
    }

    // IDK
    public static List<KeyBinding> getConflicting(InputUtil.Key key) {
        return conflictingKeys.get(key);
    }
}
