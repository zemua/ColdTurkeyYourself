package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.BooleanWrap;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class TimeLogHandler {

    private static final String TAG = "TIME_LOG_HANDLER";

    private Context mContext;
    private LifecycleOwner mLifecycleOwner;
    private TimeLoggerRepository timeLoggerRepository;
    private AppToGroupRepository appToGroupRepository;
    private ConditionToGroupRepository conditionToGroupRepository;
    private LiveData<List<ConditionToGroup>> mConditionsLiveData;
    private Map<String, AppToGroup> appsToGroupsVSpackageNameMap; // to get the group from the packagename
    private Map<Integer, List<ConditionToGroup>> conditionToGroupListVSgroupIdMap; // to get list of conditions for each group
    private List<LiveData<List<TimeLogger>>> listOfLoggerLiveDatas; // for removing observers before re-observing new ones
    private Map<String, TimeSummary> timeSummaryMap; // to get the time for each group and condition
    private TimeLogger timeLogger;
    private Long dayRefreshed = 0L;

    public TimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner){
        mContext = context;
        mLifecycleOwner = lifecycleOwner;

        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
        appToGroupRepository = AppToGroupRepository.getRepo(application);
        appToGroupRepository.findAllAppToGroup().observe(lifecycleOwner, new Observer<List<AppToGroup>>() {
            @Override
            public void onChanged(List<AppToGroup> appToGroups) {
                appsToGroupsVSpackageNameMap = appToGroups.stream().collect(Collectors.toMap(AppToGroup::getAppName, app -> app));
            }
        });

        conditionToGroupListVSgroupIdMap = new HashMap<>();
        listOfLoggerLiveDatas = new ArrayList<>();
        timeSummaryMap = new HashMap<>();
        conditionToGroupRepository = ConditionToGroupRepository.getRepo(application);
        mConditionsLiveData = conditionToGroupRepository.findAllConditionToGroup();
        refresh();
    }

    private class TimeSummary {
        private Integer groupId;
        private Integer conditionGroupId;
        private Long summedTime;
        TimeSummary(Integer groupId, Integer conditionGroupId, Long summedTime) {
            this.groupId = groupId;
            this.conditionGroupId = conditionGroupId;
            this.summedTime = summedTime;
        }
        TimeSummary(Integer groupId, Integer conditionGroupId) {
            this.groupId = groupId;
            this.conditionGroupId = conditionGroupId;
        }
        String getKey() { return "A" + groupId + "B" + conditionGroupId; }
        Integer getGroupId() { return groupId; }
        Integer getConditionGroupId() { return conditionGroupId; }
        Long getSummedTime() { return summedTime; }
    }

    private void initTimeLogger(){
        if (timeLogger == null) {
            timeLogger = new TimeLogger();
        }
    }

    private void clearTimeLoggerReference() {
        timeLogger = null;
    }

    public void insertTimeBadApp(Long millis) throws Exception {
        initTimeLogger();
        setCurrent(millis);
        decrease(millis);
        timeLogger.setPositivenegative(TimeLogger.Type.NEGATIVE);
        send();
    }

    public void insertTimeGoodApp(Long millis) throws Exception {
        initTimeLogger();
        setCurrent(millis);
        increase(millis);
        timeLogger.setPositivenegative(TimeLogger.Type.POSITIVE);
        send();
    }

    public void insertTimeNeutralApp(Long millis) throws Exception {
        initTimeLogger();
        setCurrent(millis);
        maintain();
        timeLogger.setPositivenegative(TimeLogger.Type.NEUTRAL);
        send();
    }

    private void send() throws Exception {
        if (!checkIfDataEnoughAndSubmit()){
            throw new Exception("Data provided in the submitted TimeLogger is not enough");
        }
    }

    private boolean checkIfDataEnoughAndSubmit() {
        if (timeLogger.getCountedtimemilis() == null) {
            Log.d(TAG, "CountedTime for summatory purposes not set for timeLogger");
            return false;
        }
        else if (timeLogger.getPackageName() == null){
            Log.d(TAG, "Package Name is not set for timeLogger");
            return false;
        }
        else if (timeLogger.getMillistimestamp() == null) {
            Log.d(TAG, "TimeStamp is not set for timeLogger");
            return false;
        }
        else if (timeLogger.getPositivenegative() == null) {
            Log.d(TAG, "Positive/Negative/Neutral Type is not set for timeLogger");
            return false;
        }
        else if (timeLogger.getUsedtimemilis() == null) {
            Log.d(TAG, "UsedTime is not set for timeLogger");
            return false;
        }
        else {
            submitTimeLogger();
            return true;
        }
    }

    private void submitTimeLogger() {
        timeLoggerRepository.insert(timeLogger);
        timeLogger = null;
    }

    private void setCurrent(Long millis) {
        timeLogger.setUsedtimemilis(millis);
        timeLogger.setMillistimestamp(System.currentTimeMillis());
    }

    private void increase(Long milis) {
        if (milis >= 0){
            timeLogger.setCountedtimemilis(milis);
        } else {
            timeLogger.setCountedtimemilis(-milis);
        }
    }

    private void decrease(Long milis) {
        if (milis <= 0) {
            timeLogger.setCountedtimemilis(milis);
        } else {
            timeLogger.setCountedtimemilis(-milis);
        }
    }

    private void maintain() {
        timeLogger.setCountedtimemilis(0L);
    }

    public void setGroupId(Integer groupId) {
        initTimeLogger();
        timeLogger.setGroupId(groupId);
    }

    public void setPackageName(String packageName) {
        initTimeLogger();
        timeLogger.setPackageName(packageName);
    }

    public void setPackageNameAndAutoAssignGroupId(String packageName) {
        initTimeLogger();
        timeLogger.setPackageName(packageName);
        assignGroupIdFromPackageName(packageName);
    }

    private void assignGroupIdFromPackageName(String name) {
        if (appsToGroupsVSpackageNameMap.containsKey(name)) {
            timeLogger.setGroupId(appsToGroupsVSpackageNameMap.get(name).getGroupId());
        } else {
            Log.d(TAG, "no package found in appsToGroupsVSpackageNameMap for assignGroupIdFromPackageName");
            timeLogger.setGroupId(null);
        }
    }

    private Long days(Long milliseconds) {return TimeUnit.MILLISECONDS.toDays(milliseconds);}
    private Long millis(Long days) {return TimeUnit.DAYS.toMillis(days);}
    private Long currentDay() {return days(System.currentTimeMillis());}
    private Long offsetDay(Long nDays) {return currentDay()-nDays;}
    private Long offsetDayInMillis(Long nDays) {return millis(offsetDay(nDays));}

    public void refresh() {
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        if (dayRefreshed != currentDay()){
            dayRefreshed = currentDay();
            refreshConditionsObserver();
        }
    }

    private void refreshConditionsObserver() {
        clearConditionsObserver();
        mConditionsLiveData.observe(mLifecycleOwner, new Observer<List<ConditionToGroup>>() {
            @Override
            public void onChanged(List<ConditionToGroup> conditionToGroups) {
                conditionToGroupListVSgroupIdMap.clear();
                clearLogObservers();
                conditionToGroups.stream().forEach(c -> {
                    if (c.getType().equals(ConditionToGroup.ConditionType.GROUP)) {
                        observeTimeLoggedOnCondition(c);
                    } else if (c.getType().equals(ConditionToGroup.ConditionType.FILE)) {
                        // TODO observe the time from files
                    }
                });
            }
        });
    }

    private void clearConditionsObserver() {
        clearLogObservers();
        mConditionsLiveData.removeObservers(mLifecycleOwner);
    }

    private void addConditionToMap(ConditionToGroup condition) {
        if (!conditionToGroupListVSgroupIdMap.containsKey(condition.getGroupid())) {
            conditionToGroupListVSgroupIdMap.put(condition.getGroupid(), new ArrayList<>());
        }
        conditionToGroupListVSgroupIdMap.get(condition.getGroupid()).add(condition);
    }

    private void clearLogObservers() {
        listOfLoggerLiveDatas.stream().forEach(ld -> {
            ld.removeObservers(mLifecycleOwner);
        });
        timeSummaryMap.clear();
    }

    private void observeTimeLoggedOnCondition(ConditionToGroup c) {
        addConditionToMap(c);
        LiveData<List<TimeLogger>> timesLogged = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getGroupid());
        listOfLoggerLiveDatas.add(timesLogged);
        timesLogged.observe(mLifecycleOwner, new Observer<List<TimeLogger>>() {
            @Override
            public void onChanged(List<TimeLogger> timeLoggers) {
                Long sum = timeLoggers.stream().collect(Collectors.summingLong(t -> t.getCountedtimemilis()));
                TimeSummary summary = new TimeSummary(c.getGroupid(), c.getConditionalgroupid(), sum);
                timeSummaryMap.put(summary.getKey(), summary);
            }
        });
    }

    private Long getTimeCountedOnGroupCondition(Integer groupId, Integer conditionId) {
        TimeSummary t = new TimeSummary(groupId, conditionId);
        String key = t.getKey();
        Long time;
        if (timeSummaryMap.containsKey(key)) {
            time = timeSummaryMap.get(key).getSummedTime();
        } else {
            Log.d(TAG, "no time found on timeSummaryMap for getTimeCountedOnGroupCondition");
            time = 0L;
        }
        return time;
    }

    private Long getMillisRequiredOnGroupCondition(Integer groupId, Integer conditionId) {
        if (conditionToGroupListVSgroupIdMap.containsKey(groupId)) {
            ConditionToGroup con = new ConditionToGroup();
            conditionToGroupListVSgroupIdMap.get(groupId).stream().forEach(c -> {
                if (c.getId().equals(conditionId)) {
                    con.cloneCondition(c);
                }
            });
            return MilisToTime.getMilisDeMinutos(con.getConditionalminutes());
        }
        Log.d(TAG, "no matching condition found for getMillisRequiredOnGroupCondition");
        return 0L;
    }

    private boolean ifConditionMet(Integer groupId, Integer conditionId) {
        if (getTimeCountedOnGroupCondition(groupId, conditionId) >= getMillisRequiredOnGroupCondition(groupId, conditionId)){
            return true;
        } return false;
    }

    private List<Integer> getListOfConditionIdsOfGroup(Integer groupId) {
        if (conditionToGroupListVSgroupIdMap.containsKey(groupId)){
            return conditionToGroupListVSgroupIdMap.get(groupId).stream().map(con -> con.getId()).collect(Collectors.toList());
        } else {
            Log.d(TAG, "no group found on conditionToGroupListVSgroupIdMap for getListOfConditionIdsOfGroup");
            return new ArrayList<>();
        }
    }

    private boolean ifAllGroupConditionsMet(Integer groupId) {
        BooleanWrap b = new BooleanWrap();
        b.set(true);
        getListOfConditionIdsOfGroup(groupId).stream().forEach(c -> {
            if (!ifConditionMet(groupId, c)){
                b.set(false);
            }
        });
        return b.get();
    }

    public boolean ifAllAppConditionsMet(String packageName) {
        if (appsToGroupsVSpackageNameMap.containsKey(packageName)){
            Integer groupId = appsToGroupsVSpackageNameMap.get(packageName).getGroupId();
            return ifAllGroupConditionsMet(groupId);
        } else {
            Log.d(TAG, "no packageName found in appsToGroupsVSpackageNameMap for ifAllAppConditionsMet");
            return false;
        }
    }

}
