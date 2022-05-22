package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;

public class ExternalTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    public static String TEXT_MIME_TYME = "text/plain";

    private ActivityResultLauncher<Intent> openFileLauncher;
    private List<ElementToGroup> mExistingElements;

    private RecyclerView mRecyclerView;
    private Button mButton;
    private Handler mainHandler;
    private Context mContext;
    private ViewModelProvider.Factory viewModelFactory;
    private Integer mGroupId;
    private ExternalAdapter mExternalAdapter;
    private ElementToGroupViewModel elementToGroupViewModel;
    private GroupType type;

    public ExternalTabFragment(GroupType type, Integer groupId) {
        super();
        this.type = type;
        this.mGroupId = groupId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        openFileLauncher = getOpenFileLauncher();

        mContext = getActivity();
        mainHandler = new Handler(mContext.getMainLooper());

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
        }

        View v = inflater.inflate(R.layout.fragment_button_and_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mButton = v.findViewById(R.id.button);

        mExternalAdapter = new ExternalAdapter(mContext, mGroupId);
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        elementToGroupViewModel = new ViewModelProvider(this, viewModelFactory).get(ElementToGroupViewModel.class);
        elementToGroupViewModel.findElementsOfGroupAndType(mGroupId, ElementType.FILE).observe(getViewLifecycleOwner(), (elements) -> {
            spinner.setVisibility(View.GONE);
            mExistingElements = elements;
            mExternalAdapter.updateDataSet(elements.stream().map(ElementToGroup::getName).collect(Collectors.toList()));
            mExternalAdapter.loopedGroupDbLoad(elements);
        });

        mRecyclerView.setAdapter(mExternalAdapter);
        LinearLayoutManager layoutExternal = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutExternal);

        mExternalAdapter.addFeedbackListener((tipo, feedback, parameters) -> {
            switch (tipo) {
                case ExternalAdapter.FEEDBACK_DEL_FILETOGROUP:
                    elementToGroupViewModel.deleteById(feedback.getId());
                    break;
            }
        });

        mButton.setText(R.string.anhadir_archivo);
        mButton.setOnClickListener((view) -> openFile());

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outstate) {
        outstate.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        super.onSaveInstanceState(outstate);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(TEXT_MIME_TYME);
        openFileLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> getOpenFileLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri == null) {
                    return;
                }
                if (isInExisting(uri.toString())) {
                    Toast.makeText(mContext, R.string.no_anhadido_ya_existe, Toast.LENGTH_SHORT).show();
                    return;
                }
                mContext.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                elementToGroupViewModel.insert(new ElementToGroup().withGroupId(mGroupId).withToId(-1L).withName(uri.toString()).withType(ElementType.FILE));
            }
        });
    }

    // don't know why, but using mExistingElements.contains(s) doesn't work, and filtering a stream based on "s" the same
    private boolean isInExisting(String s) {
        for (int i=0; i<mExistingElements.size(); i++) {
            String uri = mExistingElements.get(i).getName();
            if (s.equals(uri)) {
                return true;
            }
        }
        return false;
    }

}
