package devs.mrp.coolyourturkey.configuracion;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import devs.mrp.coolyourturkey.R;

@ExtendWith(MockitoExtension.class)
class MisPreferenciasTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private MisPreferencias misPreferencias;

    private PreferencesEnum propertyName = PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK;

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        sharedPreferences = mock(SharedPreferences.class);
        when(context.getSharedPreferences("apk.cool.your.turkey.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE))
                .thenReturn(sharedPreferences);
        when(context.getString(R.string.apk_cool_your_turkey_preference_file_key)).thenReturn("apk.cool.your.turkey.PREFERENCE_FILE_KEY");
        misPreferencias = new MisPreferencias(context);
    }

    @Test
    void setBoolean() {
        editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        misPreferencias.setBoolean(propertyName, true);
        verify(editor, times(1)).putBoolean(propertyName.getValue(), true);
        verify(editor, times(1)).apply();
    }

    @Test
    void getBoolean() {
        misPreferencias.getBoolean(propertyName, true);
        verify(sharedPreferences, times(1)).getBoolean(propertyName.getValue(), true);
    }
}