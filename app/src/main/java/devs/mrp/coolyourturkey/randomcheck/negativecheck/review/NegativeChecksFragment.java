package devs.mrp.coolyourturkey.randomcheck.negativecheck.review;

import android.view.View;
import android.widget.EditText;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.randomcheck.AbstractChecksFragment;

public class NegativeChecksFragment extends AbstractChecksFragment<Check> {

    @Override
    protected void initializeOtherFields(View v) {

    }

    @Override
    protected void doStuffIfNew() {

    }

    @Override
    protected void doStuffIfExisting() {

    }

    @Override
    protected void fillOtherFields(View v) {

    }

    @Override
    protected boolean assertOtherValidFields() {
        return true;
    }

    @Override
    protected void setNameHint(EditText name) {
        name.setHint(R.string.random_check_negative_name_hint);
    }

    @Override
    protected void setQuestionHint(EditText question) {
        question.setHint(R.string.random_check_negative_question_hint);
    }

    @Override
    protected Check getNewCheck() {
        return new CheckFactory().newNegative();
    }

    @Override
    protected void setupOtherObservers() {

    }
}
