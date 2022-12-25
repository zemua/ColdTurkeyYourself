package devs.mrp.coolyourturkey.commonmodules;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.FragmentComponent;

@Module
@InstallIn(FragmentComponent.class)
public class ViewModelProviderModule {

    @Provides
    public ViewModelProvider provideViewModelProvider(Application application, Fragment fragment) {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        return new ViewModelProvider(fragment, viewModelFactory);
    }
}
