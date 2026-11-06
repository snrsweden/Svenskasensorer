
package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;

import com.atak.plugins.impl.PluginContextProvider;
import com.atak.plugins.impl.PluginLayoutInflater;

import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;
import gov.tak.api.ui.IHostUIService;
import gov.tak.api.ui.Pane;
import gov.tak.api.ui.PaneBuilder;
import gov.tak.api.ui.ToolbarItem;
import gov.tak.api.ui.ToolbarItemAdapter;
import gov.tak.platform.marshal.MarshalManager;

public class PluginTemplate implements IPlugin {

    IServiceController serviceController;
    Context pluginContext;
    IHostUIService uiService;
    ToolbarItem toolbarItem;
    Pane templatePane;
    private static final String[] SYMBOLS = {"Sensor", "Larmmina", "RPAS", "Sensorområde", "Enkelpost", "Dubbelpost", "Patrull"};
    private static final String[] FILES = {"sensor", "larmmina", "rpas", "sensoromrade", "enkelpost", "dubbelpost", "patrull"};
    private static final String[] COLORS = {"Svart", "Blå", "Röd", "Grön", "Gul", "Vit"};
    private static final String[] COLOR_FILES = {"svart", "bla", "rod", "gron", "gul", "vit"};

    public PluginTemplate(IServiceController serviceController) {
        this.serviceController = serviceController;
        final PluginContextProvider ctxProvider = serviceController
                .getService(PluginContextProvider.class);
        if (ctxProvider != null) {
            pluginContext = ctxProvider.getPluginContext();
            pluginContext.setTheme(R.style.ATAKPluginTheme);
        }

        // obtain the UI service
        uiService = serviceController.getService(IHostUIService.class);

        // initialize the toolbar button for the plugin

        // create the button and set the identifier to be well known
        // if you fail to do this, the toolbar configuration will never
        // be able to find it again after the user moves the icon.
        toolbarItem = new ToolbarItem.Builder(
                pluginContext.getString(R.string.app_name),
                MarshalManager.marshal(
                        pluginContext.getResources().getDrawable(R.drawable.ic_launcher),
                        android.graphics.drawable.Drawable.class,
                        gov.tak.api.commons.graphics.Bitmap.class))
                .setListener(new ToolbarItemAdapter() {
                    @Override
                    public void onClick(ToolbarItem item) {
                        showPane();
                    }
                }).setIdentifier(pluginContext.getPackageName())
                .build();
    }

    @Override
    public void onStart() {
        // the plugin is starting, add the button to the toolbar
        if (uiService == null)
            return;

        uiService.addToolbarItem(toolbarItem);
    }

    @Override
    public void onStop() {
        // the plugin is stopping, remove the button from the toolbar
        if (uiService == null)
            return;

        uiService.removeToolbarItem(toolbarItem);
    }


    private void initializeChooser(View root) {
        Spinner symbol = root.findViewById(R.id.symbol_spinner);
        Spinner color = root.findViewById(R.id.color_spinner);
        ImageView preview = root.findViewById(R.id.symbol_preview);
        SeekBar angle = root.findViewById(R.id.angle_seek);
        TextView angleLabel = root.findViewById(R.id.angle_label);
        EditText name = root.findViewById(R.id.symbol_name);
        Button save = root.findViewById(R.id.save_button);
        symbol.setAdapter(new ArrayAdapter<>(pluginContext, android.R.layout.simple_spinner_dropdown_item, SYMBOLS));
        color.setAdapter(new ArrayAdapter<>(pluginContext, android.R.layout.simple_spinner_dropdown_item, COLORS));
        SharedPreferences prefs = pluginContext.getSharedPreferences("sensor_settings", Context.MODE_PRIVATE);
        symbol.setSelection(prefs.getInt("symbol", 0));
        color.setSelection(prefs.getInt("color", 0));
        angle.setProgress(prefs.getInt("angle", 0));
        name.setText(prefs.getString("name", ""));
        Runnable refresh = () -> {
            int degrees = angle.getProgress();
            angleLabel.setText("Riktning: " + degrees + "° / " + Math.round(degrees * 6400.0 / 360.0) + " mils");
            try (InputStream stream = pluginContext.getAssets().open("svenska_sensorer/" + COLOR_FILES[color.getSelectedItemPosition()] + "/" + FILES[symbol.getSelectedItemPosition()] + ".png")) {
                preview.setImageDrawable(new BitmapDrawable(pluginContext.getResources(), BitmapFactory.decodeStream(stream)));
                preview.setRotation(degrees);
            } catch (Exception e) {
                preview.setImageDrawable(null);
            }
        };
        android.widget.AdapterView.OnItemSelectedListener listener = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { refresh.run(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        };
        symbol.setOnItemSelectedListener(listener);
        color.setOnItemSelectedListener(listener);
        angle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { refresh.run(); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        save.setOnClickListener(v -> {
            prefs.edit().putInt("symbol", symbol.getSelectedItemPosition()).putInt("color", color.getSelectedItemPosition())
                    .putInt("angle", angle.getProgress()).putString("name", name.getText().toString()).apply();
            Toast.makeText(pluginContext, "Val sparade lokalt", Toast.LENGTH_SHORT).show();
        });
        refresh.run();
    }

    private void showPane() {
        // instantiate the plugin view if necessary
        if(templatePane == null) {
            // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
            // In this case, using it is not necessary - but I am putting it here to remind
            // developers to look at this Inflator

            View root = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null);
            initializeChooser(root);
            templatePane = new PaneBuilder(root)
                    // relative location is set to default; pane will switch location dependent on
                    // current orientation of device screen
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    // pane will take up 50% of screen width in landscape mode
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    // pane will take up 50% of screen height in portrait mode
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }

        // if the plugin pane is not visible, show it!
        if(!uiService.isPaneVisible(templatePane)) {
            uiService.showPane(templatePane, null);
        }
    }
}
