package devs.mrp.coolyourturkey.commonmodules;

import android.app.Application;
import android.app.Service;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import devs.mrp.coolyourturkey.comun.Notificador;

@Module
@InstallIn(ServiceComponent.class)
public class NotificadorModule {

    @Provides
    public Notificador provideNotificador(Service service, Application app) {
        return new Notificador(app, service);
    }

}
