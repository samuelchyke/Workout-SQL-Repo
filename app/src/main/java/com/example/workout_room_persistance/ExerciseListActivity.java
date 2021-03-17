package com.example.workout_room_persistance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.workout_room_persistance.adapter.ExercisesRecyclerAdapter;
import com.example.workout_room_persistance.adapter.WorkoutsRecyclerAdapter;
import com.example.workout_room_persistance.model.Exercise;
import com.example.workout_room_persistance.model.Workout;
import com.example.workout_room_persistance.persistance.WorkoutRepository;
import com.example.workout_room_persistance.util.VerticalSpacingItemDecorator;

import java.util.ArrayList;

public class ExerciseListActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher,
        ExercisesRecyclerAdapter.OnExerciseListener
{

    private static final String TAG = "ExerciseListActivity";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    //Ui Components
    private EditText mEditTextTitle;
    private TextView mTextViewTitle;
    private RelativeLayout mRelativeLayoutCheckContainer, mRelativeLayoutBackContainer;
    private ImageButton mToolbarCheck, mToolbarBack;
    private RecyclerView mRecyclerView;

    //Vars
    private boolean mIsNewWorkout;
    private Workout mInitialWorkout;
    private Workout mFinalWorkout;
    private GestureDetector mGestureDetector;
    private int mMode;

    // Variables
    private ArrayList<Exercise> mExercises = new ArrayList();
    private ExercisesRecyclerAdapter mExercisesRecyclerAdapter;
    private WorkoutRepository mWorkoutRepository;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        mWorkoutRepository = new WorkoutRepository(this);

        mEditTextTitle = findViewById(R.id.edit_text_toolbar_title);
        mTextViewTitle = findViewById(R.id.text_view_toolbar_title);
        mRelativeLayoutBackContainer = findViewById(R.id.back_arrow_container);
        mRelativeLayoutCheckContainer = findViewById(R.id.check_container);
        mToolbarCheck = findViewById(R.id.toolbar_check);
        mToolbarBack = findViewById(R.id.toolbar_back_arrow);

        if(getIncomingIntent()){
            //new note (EDIT MODE)
            setNewWorkoutProperties();
            enableEditMode();

        }
        else{
            //NOT a new note (VIEW MODE)
            setWorkoutProperties();

        }

        mRecyclerView = findViewById(R.id.recycler_view);
        initRecyclerView();
        insertFakeExercises();

        setListener();

    }

    private boolean getIncomingIntent(){
        if(getIntent().hasExtra("selected_workout")){
            mInitialWorkout = getIntent().getParcelableExtra("selected_workout");

            mFinalWorkout = new Workout();
            mFinalWorkout.setTitle(mInitialWorkout.getTitle());
//            mFinalWorkout.setExercises(mInitialWorkout.getExercises());
            mFinalWorkout.setId(mInitialWorkout.getId());

            Log.d(TAG, "getIncomingIntent: " + mInitialWorkout.toString());
            mIsNewWorkout = false;
            mMode = EDIT_MODE_DISABLED;
            return false;
        }
        mMode = EDIT_MODE_ENABLED;
        mIsNewWorkout = true;
        return true;
    }

    private void enableEditMode(){
        mRelativeLayoutBackContainer.setVisibility(View.GONE);
        mRelativeLayoutCheckContainer.setVisibility(View.VISIBLE);

        mTextViewTitle.setVisibility(View.GONE);
        mEditTextTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;
    }

    private void disableEditMode(){
        mRelativeLayoutBackContainer.setVisibility(View.VISIBLE);
        mRelativeLayoutCheckContainer.setVisibility(View.GONE);

        mTextViewTitle.setVisibility(View.VISIBLE);
        mEditTextTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;
        mFinalWorkout.setTitle(mEditTextTitle.getText().toString());
        Log.d(TAG, "disableEditMode: called");
        saveChanges();
    }

    private void saveChanges(){
        if(mIsNewWorkout){
            saveNewWorkout();
        }else{
            updateWorkout();
        }
    }


    private void saveNewWorkout(){mWorkoutRepository.insertWorkoutTask(mFinalWorkout); }
    private void updateWorkout(){mWorkoutRepository.updateWorkout(mFinalWorkout);}

    private void setWorkoutProperties(){
        mEditTextTitle.setText(mInitialWorkout.getTitle());
        mTextViewTitle.setText(mInitialWorkout.getTitle());
    }

    private void setNewWorkoutProperties(){
        mEditTextTitle.setText("Note Title");
        mTextViewTitle.setText("Note Title");

        mInitialWorkout = new Workout();
        mFinalWorkout = new Workout();
        mInitialWorkout.setTitle("Note Title");
        mFinalWorkout.setTitle("Note Title");
    }


    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setListener(){

        mGestureDetector = new GestureDetector(this,this);
        mTextViewTitle.setOnClickListener(this);
        mToolbarCheck.setOnClickListener(this);
        mToolbarBack.setOnClickListener(this);
        mEditTextTitle.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTextViewTitle.setText(s.toString());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
                    return false;
                }
    @Override
    public boolean onDoubleTap(MotionEvent e) {
                    return false;
                }
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        enableEditMode();
        Log.d(TAG, "onDoubleTab: double tapped!");
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.toolbar_check:{
                hideSoftKeyboard();
                disableEditMode();
                break;
            }

            case R.id.text_view_toolbar_title:{
                enableEditMode();
                mEditTextTitle.requestFocus();
                //Sets cursor at end of string
                mEditTextTitle.setSelection(mEditTextTitle.length());
                break;
            }

            case R.id.toolbar_back_arrow:{
                finish();
                break;
            }

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if(mMode == EDIT_MODE_ENABLED){
            //Clicks check mark
            onClick(mToolbarCheck);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if(mMode == EDIT_MODE_ENABLED){
            enableEditMode();
        }
    }

    // Exercise Recycle View
    @Override
    public void onExerciseClicked(int position) {
        mExercisesRecyclerAdapter.getItemViewType(position);




    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(mRecyclerView);
        mExercisesRecyclerAdapter = new ExercisesRecyclerAdapter(mExercises, this);
        mRecyclerView.setAdapter(mExercisesRecyclerAdapter);

    }

    public void insertFakeExercises(){
        for(int i = 0;i<20; i++){

            Exercise noteExercise = new Exercise();
            noteExercise.setTitle("Exercise #"+ i);
            noteExercise.setRepetitions(""+i);
            mExercises.add(noteExercise);
        }
        mExercisesRecyclerAdapter.notifyDataSetChanged();
    }

    private void deleteExercise(Exercise exercise){
        mExercises.remove(exercise);
        mExercisesRecyclerAdapter.notifyDataSetChanged();
    }

    private void focusExercise(Exercise exercise){
        mExercisesRecyclerAdapter.notifyDataSetChanged();
    }

    public void enableExerciseEditMode(){

    }

    private void disableExerciseEditMode(){

    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteExercise(mExercises.get(viewHolder.getAdapterPosition()));
        }
    };
}