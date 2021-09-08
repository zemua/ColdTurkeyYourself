package devs.mrp.coolyourturkey.randomcheck.timeblocks.export;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export.TimeBlockExport;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export.TimeBlockExportRepository;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;

public class TimeBlockExporter {

    private final Long TIME_BETWEEN_FILES_REFRESH = 60*1000*1L; // 1 minute between file refreshes

    private LiveData<List<TimeBlockExport>> mTimeBlockExportLiveData; // to clear observer from exports
    private Observer<List<TimeBlockExport>> mTimeBlockExportObserver; // to clear observer from exports

    private Map<LiveData<List<TimeBlockLogger>>, Observer<List<TimeBlockLogger>>> mMapOfLoggerLiveDataObserversByBlockId; // to clear observers from livedata only

    private List<TimeBlockExport> mBlockExportList;
    private Map<Integer, List<TimeBlockLogger>> mTimeLoggersByBlockId;

    private TimeBlockLoggerRepository mLoggerRepo;
    private TimeBlockExportRepository mExportRepo;
    private LifecycleOwner mLifecycleOwner;
    private Context mContext;
    private Application mApplication;

    private long mLastFilesChecked = 0;
    private Long dayRefreshed = 0L;

    private Handler mMainHandler;

    public TimeBlockExporter(Application app, LifecycleOwner owner, Context context) {
        mMainHandler = new Handler(Looper.getMainLooper());
        mBlockExportList = new ArrayList<>();
        mTimeLoggersByBlockId = new HashMap<>();
        mMapOfLoggerLiveDataObserversByBlockId = new HashMap<>();
        mLoggerRepo = TimeBlockLoggerRepository.getRepo(app);
        mExportRepo = TimeBlockExportRepository.getRepo(app);
        mTimeBlockExportLiveData = mExportRepo.findAllTimeBlockExport();
        mLifecycleOwner = owner;
        mContext = context;
        mApplication = app;
        setExportObservers();
    }

    public void refresh() {
        refreshTimeExportedToFiles();
        refreshDayCounting();
    }

    private void setExportObservers() {
        mMainHandler.post(() -> {
            if (mTimeBlockExportObserver != null && mTimeBlockExportLiveData != null) {
                mTimeBlockExportLiveData.removeObserver(mTimeBlockExportObserver);
            }
            mTimeBlockExportObserver = new Observer<List<TimeBlockExport>>() {
                @Override
                public void onChanged(List<TimeBlockExport> timeBlockExports) {
                    try { clearExportObservers();} catch (Exception e) {e.printStackTrace();}
                    mBlockExportList = timeBlockExports;
                    timeBlockExports.stream().forEach(export -> {
                        Observer<List<TimeBlockLogger>> observer = new Observer<List<TimeBlockLogger>>() {
                            @Override
                            public void onChanged(List<TimeBlockLogger> timeBlockLoggers) {
                                mTimeLoggersByBlockId.put(export.getBlockid(), timeBlockLoggers);
                            }
                        };
                        LiveData<List<TimeBlockLogger>> liveData = mLoggerRepo.findByTimeNewerAndBlockId(offsetDayInMillis(export.getDays().longValue()), export.getBlockid());
                        mMapOfLoggerLiveDataObserversByBlockId.put(liveData, observer);
                        liveData.observe(mLifecycleOwner, observer);
                    });
                }
            };
            mTimeBlockExportLiveData.observe(mLifecycleOwner, mTimeBlockExportObserver);
        });
    }

    private void clearExportObservers() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mMapOfLoggerLiveDataObserversByBlockId.keySet().stream().forEach(livedata -> livedata.removeObserver(mMapOfLoggerLiveDataObserversByBlockId.get(livedata)));
            mMapOfLoggerLiveDataObserversByBlockId.clear();
        } else {
            throw new Exception("clearExportObservers shall be called from the main thread");
        }
    }

    private void refreshTimeExportedToFiles() {
        long now = System.currentTimeMillis();
        if (now - TIME_BETWEEN_FILES_REFRESH > mLastFilesChecked && mBlockExportList != null) {
            mLastFilesChecked = now;
            mBlockExportList.stream().forEach(export -> {
                if (FileReader.ifHaveReadingRights(mContext, Uri.parse(export.getArchivo()))) {
                    Long timeMillis = mTimeLoggersByBlockId.get(export.getBlockid()).stream().collect(Collectors.summingLong(logger -> logger.getTimecounted()));
                    FileReader.writeTextToUri(mApplication, Uri.parse(export.getArchivo()), daysToFileFormat(export.getDays(), timeMillis));
                }
            });
        }
    }

    private String daysToFileFormat(Integer offsetDays, Long millisToAppend) {
        // Format is:
        // YYYY-MM-DD-XXXXX

        Long millis = offsetDayInMillis(offsetDays.longValue());

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);

        int year = date.get(Calendar.YEAR);
        int monthnum = date.get(Calendar.MONTH);
        int daynum = date.get(Calendar.DAY_OF_MONTH);

        String month;
        if (monthnum<10) {
            month = "0"+monthnum;
        } else {
            month = String.valueOf(monthnum);
        }

        String day;
        if (daynum<10) {
            day = "0"+daynum;
        } else {
            day = String.valueOf(daynum);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(year)).append("-")
                .append(month).append("-")
                .append(day).append("-")
                .append(String.valueOf(millisToAppend));
        return builder.toString();
    }

    private Long days(Long milliseconds) {return TimeUnit.MILLISECONDS.toDays(milliseconds);}
    private Long millis(Long days) {return TimeUnit.DAYS.toMillis(days);}
    private Long currentDay() {return days(System.currentTimeMillis());}
    private Long offsetDay(Long nDays) {return currentDay()-nDays;}
    private Long offsetDayInMillis(Long nDays) {return millis(offsetDay(nDays));}

    private void refreshDayCounting() {
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        Long currentDay = currentDay();
        if (!dayRefreshed.equals(currentDay) && mBlockExportList != null){
            dayRefreshed = currentDay;
            setExportObservers();
        }
    }

}
