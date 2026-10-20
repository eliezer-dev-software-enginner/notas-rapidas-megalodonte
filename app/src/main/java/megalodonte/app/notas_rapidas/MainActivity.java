package megalodonte.app.notas_rapidas;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import megalodonte.android.bridge.AndroidJsoncScreen;
import megalodonte.android.bridge.MegalodonteActivity;
import megalodonte.application.AndroidRouter;
import megalodonte.application.MegalodonteApp;
import megalodonte.base.theme.ThemeManager;
import megalodonte.theme.DefaultTheme;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public final class MainActivity extends MegalodonteActivity {
    private JSONArray notes;
    private AndroidRouter router;
    private int editing = -1;

    @Override protected void main() {
        try { notes = new JSONArray(getPreferences(0).getString("notes", "[]")); }
        catch (JSONException error) { throw new IllegalStateException(error); }
        ThemeManager.setTheme(new DefaultTheme());
        MegalodonteApp.run(context -> {
            router = new AndroidRouter(context)
                    .route("/notes", this::listScreen)
                    .route("/editor", this::editorScreen);
            router.start("/notes");
        });
    }

    private AndroidJsoncScreen load(String name) {
        try (var stream = getAssets().open("views/" + name + ".view.jsonc")) {
            return new AndroidJsoncScreen(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException error) { throw new IllegalStateException(error); }
    }

    private AndroidJsoncScreen listScreen() {
        AndroidJsoncScreen screen = load("list");
        screen.setState("notes", summary());
        screen.onAction(action -> {
            switch (action) {
                case "new" -> { editing = -1; router.navigate("/editor"); }
                case "open", "delete" -> {
                    int index;
                    try { index = Integer.parseInt(screen.getStringState("number").trim()) - 1; }
                    catch (NumberFormatException error) { screen.setState("status", "Informe o número da nota."); return; }
                    if (index < 0 || index >= notes.length()) { screen.setState("status", "Nota não encontrada."); return; }
                    if (action.equals("open")) { editing = index; router.navigate("/editor"); }
                    else { notes.remove(index); save(); screen.setState("notes", summary()); screen.setState("status", "Nota excluída."); }
                }
                default -> throw new IllegalArgumentException(action);
            }
        });
        return screen;
    }

    private AndroidJsoncScreen editorScreen() {
        AndroidJsoncScreen screen = load("editor");
        if (editing >= 0) {
            JSONObject note = notes.optJSONObject(editing);
            if (note != null) {
                screen.setState("title", note.optString("title"));
                screen.setState("body", note.optString("body"));
            }
        }
        screen.onAction(action -> {
            if (action.equals("back")) { router.back(); return; }
            if (!action.equals("save")) throw new IllegalArgumentException(action);
            String title = screen.getStringState("title").trim();
            String body = screen.getStringState("body").trim();
            if (title.isEmpty() || body.isEmpty()) { screen.setState("status", "Informe título e conteúdo."); return; }
            try {
                JSONObject note = new JSONObject().put("title", title).put("body", body);
                if (editing < 0) notes.put(note); else notes.put(editing, note);
            } catch (JSONException error) { throw new IllegalStateException(error); }
            save();
            router.back();
        });
        return screen;
    }

    private String summary() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < notes.length(); i++)
            text.append(i + 1).append(". ").append(notes.optJSONObject(i).optString("title")).append('\n');
        return text.isEmpty() ? "Nenhuma nota ainda." : text.toString();
    }

    private void save() { getPreferences(0).edit().putString("notes", notes.toString()).apply(); }

    @Override public void onBackPressed() {
        if (router == null || !router.back()) super.onBackPressed();
    }
}
