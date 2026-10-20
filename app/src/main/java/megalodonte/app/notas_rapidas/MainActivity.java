package megalodonte.app.notas_rapidas;
import java.nio.charset.StandardCharsets;
import megalodonte.android.bridge.AndroidJsoncScreen;
import megalodonte.android.bridge.MegalodonteActivity;
import megalodonte.application.MegalodonteApp;
import megalodonte.base.theme.ThemeManager;
import megalodonte.theme.DefaultTheme;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public final class MainActivity extends MegalodonteActivity {
    private JSONArray notes;
    private AndroidJsoncScreen screen;
    @Override protected void main() {
        try (var stream = getAssets().open("views/home.view.jsonc")) {
            ThemeManager.setTheme(new DefaultTheme());
            notes = new JSONArray(getPreferences(0).getString("notes", "[]"));
            screen = new AndroidJsoncScreen(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            screen.onAction(this::act);
            refresh("");
            MegalodonteApp.run(context -> context.useView(screen));
        } catch (java.io.IOException | JSONException error) { throw new IllegalStateException(error); }
    }
    private void act(String action) {
        try {
        String message;
        if (action.equals("save")) {
            String title = screen.getStringState("title").trim();
            String body = screen.getStringState("body").trim();
            if (title.isEmpty() || body.isEmpty()) { refresh("Informe título e conteúdo."); return; }
            notes.put(new JSONObject().put("title", title).put("body", body));
            screen.setState("title", ""); screen.setState("body", "");
            message = "Nota salva.";
        } else {
            int index;
            try { index = Integer.parseInt(screen.getStringState("number").trim()) - 1; }
            catch (NumberFormatException error) { refresh("Informe o número da nota."); return; }
            if (index < 0 || index >= notes.length()) { refresh("Nota não encontrada."); return; }
            if (action.equals("delete")) { notes.remove(index); message = "Nota excluída."; }
            else if (action.equals("open")) {
                JSONObject note = notes.getJSONObject(index);
                screen.setState("title", note.optString("title"));
                screen.setState("body", note.optString("body"));
                message = "Nota aberta para leitura. Salvar cria uma nova nota.";
            } else throw new IllegalArgumentException(action);
        }
        getPreferences(0).edit().putString("notes", notes.toString()).apply();
        refresh(message);
        } catch (JSONException error) { throw new IllegalStateException(error); }
    }
    private void refresh(String message) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < notes.length(); i++)
            text.append(i + 1).append(". ").append(notes.optJSONObject(i).optString("title")).append('\n');
        screen.setState("notes", text.isEmpty() ? "Nenhuma nota ainda." : text.toString());
        screen.setState("status", message);
    }
}
