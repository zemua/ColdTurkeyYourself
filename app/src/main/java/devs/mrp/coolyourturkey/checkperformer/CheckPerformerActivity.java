package devs.mrp.coolyourturkey.checkperformer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Random;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservableNegative;
import devs.mrp.coolyourturkey.comun.MyObservablePositive;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockRepository;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;
import devs.mrp.coolyourturkey.watchdog.TimePusherFactory;
import devs.mrp.coolyourturkey.watchdog.TimePusherInterface;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.RandomCheckWorker;

public class CheckPerformerActivity extends AppCompatActivity {

    private final String TAG = "CheckPerformerActivity";

    private final String BUNDLE_BLOCK_ID = "block.id";
    private final String BUNDLE_PREMIO = "premio";
    private final String BUNDLE_FINISHED = "is.finished";
    private final String BUNDLE_SUMMED = "summed";
    private final String BUNDLE_POSITIVE_QUESTION = "positive.question";
    private final String BUNDLE_NEGATIVE_QUESTION = "negative.question";
    private final String BUNDLE_NEGATIVE_DONE = "negative.done";

    private Fragment mFragment;
    private TimePusherInterface timePusher;
    private FragmentManager fm;
    private boolean summed = false;

    private String positiveQuestion = "";
    private String negativeQuestion = "";
    private boolean mIsFinished = false;
    private boolean mNegativeDone = false;

    private Integer blockId;
    private long mPremio;

    private CheckTimeBlockRepository mRepo;
    private TimeBlockLoggerRepository mLogger;
    private ElementToGroupRepository mElementRepo;
    private int mGroupId = -1;

    private MisPreferencias mPreferencias;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferencias = new MisPreferencias(this);
        if (mPreferencias.getLastRandomCheckTimeStamp() >= getIntent().getLongExtra(RandomCheckWorker.KEY_FOR_TIMESTAMP, 0l)) {
            finalizar();
            return;
        } else {
            mPreferencias.setLastRandomCheckTimeStamp(getIntent().getLongExtra(RandomCheckWorker.KEY_FOR_TIMESTAMP, 0l));
        }

        fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);

        if (savedInstanceState != null) {
            blockId = savedInstanceState.getInt(BUNDLE_BLOCK_ID);
            mIsFinished = savedInstanceState.getBoolean(BUNDLE_FINISHED);
            mNegativeDone = savedInstanceState.getBoolean(BUNDLE_NEGATIVE_DONE);
            mPremio = savedInstanceState.getLong(BUNDLE_PREMIO);
            summed = savedInstanceState.getBoolean(BUNDLE_SUMMED);
            negativeQuestion = savedInstanceState.getString(BUNDLE_NEGATIVE_QUESTION);
            positiveQuestion = savedInstanceState.getString(BUNDLE_POSITIVE_QUESTION);
            resetData(); // on fragment
        }

        setContentView(R.layout.activity_singlefragment);

        timePusher = new TimePusherFactory().get(ContadorRepository.getRepo(getApplication()));

        blockId = -1;
        if (getIntent().hasExtra(RandomCheckWorker.KEY_FOR_BLOCK_ID)) {
            blockId = getIntent().getIntExtra(RandomCheckWorker.KEY_FOR_BLOCK_ID, -1);
            if (blockId == -1 || mIsFinished) {
                finalizar();
                return;
            }
        } else {
            finalizar();
            return;
        }

        mRepo = CheckTimeBlockRepository.getRepo(getApplication());
        mLogger = TimeBlockLoggerRepository.getRepo(getApplication());
        mElementRepo = ElementToGroupRepository.getRepo(getApplication());
        LiveData<List<TimeBlockWithChecks>> ld = mRepo.getTimeBlockWithChecksById(blockId);
        ld.observe(CheckPerformerActivity.this, timeBlockWithChecks -> {
            ld.removeObservers(CheckPerformerActivity.this);

            if (timeBlockWithChecks.size() > 0) {
                TimeBlockWithChecks tbwc = timeBlockWithChecks.get(0);
                AbstractTimeBlock tb = new TimeBlockFactory().importFrom(tbwc);

                Random rand = new Random();

                PositiveCheck pCheck;
                if (tb.getPositiveChecks().size() > 0) {
                    if (positiveQuestion.isEmpty()) {
                        pCheck = tb.getPositiveChecks().get(rand.nextInt(tb.getPositiveChecks().size()));
                        positiveQuestion = pCheck.getQuestion();
                        //mPremio = ((((tb.getMaximumLapse()-tb.getMinimumLapse())/2)+tb.getMinimumLapse())*pCheck.getMultiplicador());
                        mPremio = tb.getPrizeammount()*60*1000*pCheck.getMultiplicador();
                    }
                } else {
                    finalizar();
                    return;
                }

                if (negativeQuestion.isEmpty() && tb.getNegativeChecks().size() > 0) {
                    negativeQuestion = tb.getNegativeChecks().get(rand.nextInt(tb.getNegativeChecks().size())).getQuestion();
                }


                if (positiveQuestion.equals("")) {
                    finalizar();
                    return;
                }

                //final String pq = positiveQuestion;

                if (mFragment == null) {
                    mFragment = new CheckPerformerFragment();
                    if (!negativeQuestion.equals("") && !mNegativeDone) {
                        resetData();
                        //((CheckPerformerFragment)mFragment).setData(negativeQuestion, true, false, false, this);
                    } else {
                        mNegativeDone = true;
                        resetData();
                        //((CheckPerformerFragment)mFragment).setData(positiveQuestion, false, true, true, this);
                    }
                    fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
                }

                ((MyObservableNegative<Boolean>)mFragment).addNegativeObserver((tipo, bool) -> {
                    mNegativeDone = true;
                    if (!bool) {
                        fm.beginTransaction().remove(mFragment).commit();
                        mFragment = new CheckPerformerFragment();
                        resetData();
                        //((CheckPerformerFragment)mFragment).setData(pq, false, true, true, this);
                        addPositiveObserver();
                        ((MyObservableNegative<?>) mFragment).addNegativeObserver((t, b) -> {
                            CheckPerformerActivity.this.finalizar();
                            return;
                        });
                        fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
                    } else {
                        CheckPerformerActivity.this.finalizar();
                        return;
                    }
                });

                addPositiveObserver();

            } else {
                finalizar();
                return;
            }
        });

    }

    private void addPositiveObserver() {
        ((MyObservablePositive<Boolean>)mFragment).addPositiveObserver((tipo, bool) -> {
            if (bool && !summed) {
                summed = true; // prevent double tap and so double sum
                Log.d(TAG, "to add premio " + mPremio + " en h:m:s " + mPremio/(60*60*1000) + ":" + (mPremio%(60*60*1000))/(60*1000) + ":" + (mPremio%(60*1000)/1000) );
                timePusher.add(System.currentTimeMillis(), mPremio, this);
                mLogger.insert(new TimeBlockLogger(blockId, mPremio, System.currentTimeMillis(), mGroupId));
                PrizeConfirmationFragment fr = new PrizeConfirmationFragment();
                fr.addObserver((tp, fdbck) -> {
                    CheckPerformerActivity.this.finalizar();
                });
                fm.beginTransaction().remove(mFragment).commit();
                fm.beginTransaction().add(R.id.fragment_container, fr).commit();
                //finalizar();
            } else {
                CheckPerformerActivity.this.finalizar();
            }
        });
    }

    private void setGroupId() {
        mElementRepo.findElementOfTypeAndElementId(ElementType.CHECK, blockId).observe(this, elements -> {
            if (elements.size()>0) {
                mGroupId = elements.get(0).getGroupId();
            }
        });
    }

    private void finalizar() {
        mIsFinished = true;
        //Intent intent = new Intent(CheckPerformerActivity.this, PreventsBack.class);
        //intent.putExtra(PreventsBack.INTENT_IF_PRIZE, ifPrize);
        //startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putInt(BUNDLE_BLOCK_ID, blockId);
        outstate.putBoolean(BUNDLE_FINISHED, mIsFinished);
        outstate.putBoolean(BUNDLE_NEGATIVE_DONE, mNegativeDone);
        outstate.putLong(BUNDLE_PREMIO, mPremio);
        outstate.putBoolean(BUNDLE_SUMMED, summed);
        outstate.putString(BUNDLE_NEGATIVE_QUESTION, negativeQuestion);
        outstate.putString(BUNDLE_POSITIVE_QUESTION, positiveQuestion);

        super.onSaveInstanceState(outstate);
    }

    public void resetData() {
        if (!mNegativeDone){
            ((CheckPerformerFragment)mFragment).setData(negativeQuestion, true, false, false, this);
        } else {
            ((CheckPerformerFragment)mFragment).setData(positiveQuestion, false, true, true, this);
        }
    }

    @Override
    public void onBackPressed() {}

}
