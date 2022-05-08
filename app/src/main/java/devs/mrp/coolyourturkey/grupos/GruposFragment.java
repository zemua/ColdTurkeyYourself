package devs.mrp.coolyourturkey.grupos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.grupos.grupospositivos.AddGroupActivity;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public abstract class GruposFragment<T> extends FeedbackerFragment<Object> {

    private ViewModelProvider.Factory viewModelFactory;
    private TimeLogHandler mTimeLogHandler;

    private Button mAddGrupoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grupos, container, false);
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mTimeLogHandler = new TimeLogHandler(getAttachContext(), getActivity().getApplication(), getViewLifecycleOwner());

        mAddGrupoButton = (Button) v.findViewById(R.id.button_add_group);
        mAddGrupoButton.setOnClickListener((view) -> {
            Intent intent = new Intent(getAttachContext(), AddGroupActivity.class);
        });



        return v;
    }

    public abstract void addGrupoToDb(T grupo);

    public abstract void removeGrupoFromDb(Integer id);

}
