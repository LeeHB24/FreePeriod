package xyz.capsaicine.freeperiod.activities.home;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.Model.ClassInfo;
import xyz.capsaicine.freeperiod.Model.Lecture;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.Model.PartyInDatabase;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.PivotActivity;
import xyz.capsaicine.freeperiod.activities.chat.ChatDBCtrct;
import xyz.capsaicine.freeperiod.activities.chat.ChattingActivity;
import xyz.capsaicine.freeperiod.activities.meal.MealActivity;
import xyz.capsaicine.freeperiod.activities.meal.PlayActivity;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.NetworkService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends PivotActivity {
    // instances
    private BottomNavigationView navBottom;

    // DB로 받아올 강의 목록
    private int numOfMajor = 5;
    private ArrayList<ArrayList<Lecture_List_Item>> lectureBundle = new ArrayList<>(numOfMajor); // 0 - all, 1 - 소웨, 2 - 사보, 3 - 국디, 4 - 교양

    // 추가/삭제할 강의 하나를 임시 저장하는 부분
    private Lecture_List_Item lectureListItemToAdd;
    private Lecture_List_Item lectureListItemToDelete;

    // 파티를 그려주기 위해 임시 저장
    private static ArrayList<Party> partyArrayList = new ArrayList<Party>();
    private static ArrayList<Party> partiesOnTimeTables = new ArrayList<Party>();

    // 시간표 스케쥴 겹치는지 검증기 // course list adapter를 따로 만들어야 하는지?
    private Schedule schedule;

    TimeTableCellColor timeTableCellColor;

    // 새로 그리는 textview의 높이 단위를 저장한다.
    private int timeTableBlockHeight;
    private int timeTableBlocWidth;

    // 시간표 각 셀을 저장하는데, timetable 에 수업이 뭐가 들어가 있는지 기록해준다.
    private AutoResizeTextView[][] lectureBlockTextViews = new AutoResizeTextView[5][26]; // 0 - mon, 1 - tue, 2 - wed, 3 - thu, 4 - fri
    private AutoResizeTextView[][] partyBlockTextViews = new AutoResizeTextView[5][26];

    private RelativeLayout[] timeTableDateRelativeLayouts = new RelativeLayout[5];

    private LinearLayout timeTableLinearLayout;

    private boolean isDrawedFromDB = false;

    private String testString = ""; // test 용

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        timeTableBlockHeight = timeTableDateRelativeLayouts[0].getHeight()/26; // timeblock 높이 설정
        timeTableBlocWidth = timeTableDateRelativeLayouts[0].getWidth();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        partyArrayList.clear();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 세로 화면만 지원
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navBottom = findViewById(R.id.nav_bottom);
        setPivotActivity(HomeActivity.this, navBottom);

        // initializations
        initLectureBundle(numOfMajor); // initialize lecture bundle
        initTableDateRelativeLayouts(); // 시간표의 요일별 column 의 linearlayout 을 초기화해준다.

        schedule = new Schedule();

        timeTableCellColor = new TimeTableCellColor();

        getLecturesFromDB(); // allLectures에 강의들이 저장됨
        timeTableLinearLayout = (LinearLayout)findViewById(R.id.timeTableLinearLayout);

        timeTableLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                showMoveToPartyDialog(x, y);
                return false;
            }
        });

        Button addLectureButton = (Button)findViewById(R.id.addLectureButton);

        // show dialog
        addLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLectureDialog();
            }
        });
    }

    private void showPartyInfoDialog(Party party) {
        LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout showPartyInfoLayout = (ConstraintLayout)viewInflater.inflate(R.layout.home_party_info_dialog, null);
        final AlertDialog.Builder showPartyInfoAlertBuilder = new AlertDialog.Builder(this);
        showPartyInfoAlertBuilder.setTitle("파티 정보");
        showPartyInfoAlertBuilder.setView(showPartyInfoLayout);

        final TextView partyNameTextView = (TextView)showPartyInfoLayout.findViewById(R.id.partyNameTextView);
        partyNameTextView.setText(party.getPartyName());

        final TextView partyTagTextView = (TextView)showPartyInfoLayout.findViewById(R.id.partyTagTextView);
        String partyTagString = "";
        ArrayList<String> partyTagList = party.getPartyTagList();
        for (int partyIndex = 0; partyIndex < partyTagList.size(); partyIndex++) {
            partyTagString += partyTagList.get(partyIndex) + ", ";
        }
        partyTagTextView.setText(partyTagString);
        final TextView partyCapacityTextView = (TextView)showPartyInfoLayout.findViewById(R.id.partyCapacityTextView);
        partyCapacityTextView.setText(party.getCapacityString());

        final TextView partyTimeTextView = (TextView)showPartyInfoLayout.findViewById(R.id.partyTimeText);
        partyTimeTextView.setText(party.getPartyTimeString());

        final TextView partyStatusTextView = (TextView)showPartyInfoLayout.findViewById(R.id.partyStatusTextView);
        Party.Status partyStatus = party.getPartyStatus();
        String partyStatusString = "";
        if (partyStatus == Party.Status.Recruiting) {
            partyStatusString = "Recruiting";
        } else if (partyStatus == Party.Status.Blocked) {
            partyStatusString = "Blocked";
        } else if (partyStatus == Party.Status.Closed) {
            partyStatusString = "Closed";
        } else if (partyStatus == Party.Status.Finished) {
            partyStatusString = "Finished";
        } else {
            partyStatusString = "error";
        }
        partyStatusTextView.setText(partyStatusString);

        showPartyInfoAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });

        final AlertDialog showPartyInfoDialog = showPartyInfoAlertBuilder.create();
        showPartyInfoDialog.show();
    }

    private void showLectureInfoDialog(Lecture_List_Item lecture) {
        LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout showLectureInfoLayout = (ConstraintLayout)viewInflater.inflate(R.layout.home_lecture_info_dialog, null);
        final AlertDialog.Builder showLectureAlertBuilder = new AlertDialog.Builder(this);
        showLectureAlertBuilder.setTitle("강의 정보");
        showLectureAlertBuilder.setView(showLectureInfoLayout);

        final TextView lectureNameTextVeiw = (TextView)showLectureInfoLayout.findViewById(R.id.lectureNameTextView);
        lectureNameTextVeiw.setText(lecture.getLectureName());

        final TextView profNameTextView = (TextView)showLectureInfoLayout.findViewById(R.id.profNameTextViwe);
        profNameTextView.setText(lecture.getProfName());

        final TextView lectureInfoTextView = (TextView)showLectureInfoLayout.findViewById(R.id.lectureInfoTextView);
        ArrayList<ClassInfo> classInfo = classInfoParser(lecture);
        String classInfoString = "";
        for (int classInfoIndex = 0; classInfoIndex < classInfo.size(); classInfoIndex++) {
            classInfoString += classInfo.get(classInfoIndex).getTimeStringWithDay() + "\n";
            classInfoString += classInfo.get(classInfoIndex).getClassPlace() + "\n";
        }

        lectureInfoTextView.setText(classInfoString);

        showLectureAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });

        final AlertDialog showLectureInfoDialog = showLectureAlertBuilder.create();
        showLectureInfoDialog.show();

    }

    // 시간표 상의 빈 공간을 눌렀을 때 파티를 생성하겠냐는 dialog
    private void showMoveToPartyDialog(int x, int y) { // 눌러진 좌표
        HomePositionToTimeParser p2tParser = new HomePositionToTimeParser(x, y, timeTableBlocWidth, timeTableBlockHeight);

        String dateOfClickedPosition = p2tParser.dayFromPosition();
        String dayState = p2tParser.dayStateFromPosition();
        int hourOfClickedPosition = p2tParser.getHourFromPosition();
        int minuteOfClickedPosition = p2tParser.getMinuteFromPosition();

        LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout moveToPartyLayout = (ConstraintLayout)viewInflater.inflate(R.layout.home_move_to_party_dialog, null);
        // dialog builder ; moveToPartyLayout
        final AlertDialog.Builder moveToPartyAlertBuilder = new AlertDialog.Builder(this);
        moveToPartyAlertBuilder.setTitle("파티로 이동");
        moveToPartyAlertBuilder.setView(moveToPartyLayout);

        final String partyPivotDateString = p2tParser.getDateByString();
        final TextView dateTextView = (TextView)moveToPartyLayout.findViewById(R.id.date);
        dateTextView.setText("" + dateOfClickedPosition);
        final TextView timeTextView = (TextView)moveToPartyLayout.findViewById(R.id.time);
        timeTextView.setText("" + dayState + " " + hourOfClickedPosition + " 시 " + minuteOfClickedPosition + " 분");

        final Button moveToMealActivityButton = (Button)moveToPartyLayout.findViewById(R.id.moveToMealActivityButton);
        moveToMealActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "밥 파티로 이동합니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, MealActivity.class);
                intent.putExtra("partyPivotDateString", partyPivotDateString);
                startActivity(intent);
            }
        });
        Button moveToPlayActivityButton = (Button)moveToPartyLayout.findViewById(R.id.moveToPlayActivityButton);
        moveToPlayActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "활동 파티로 이동합니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                intent.putExtra("partyPivotDateString", partyPivotDateString);
                startActivity(intent);
            }
        });
        moveToPartyAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });
        final AlertDialog moveToPartyDialog = moveToPartyAlertBuilder.create();
        moveToPartyDialog.show();
    }

    private void showMoveToChattingDialog(final Party party) {
        LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout moveToChattingLayout = (ConstraintLayout)viewInflater.inflate(R.layout.home_move_to_chatting_dialog, null);
        // dialog builder ; moveToChattingLayout
        final AlertDialog.Builder moveToChattingAlertBuilder = new AlertDialog.Builder(this);
        moveToChattingAlertBuilder.setTitle("채팅으로 이동");
        moveToChattingAlertBuilder.setView(moveToChattingLayout);

        moveToChattingAlertBuilder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 이동 코드
                Intent intent = new Intent(HomeActivity.this, ChattingActivity.class);
                intent.putExtra(ChatDBCtrct.roomId, party.getPartyId());
                startActivity(intent);
            }
        });
        moveToChattingAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });
        final AlertDialog moveToChattingDialog = moveToChattingAlertBuilder.create();
        moveToChattingDialog.show();
    }

    // 강의 textView 를 누르면 강의를 삭제하겠냐는 dialog
    private void showDelelteLectureDialog(final int lectureId, final int startTimeBlockIndex, final String lectureName, final String lectureTimeBlocksString) {
        LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout deleteLectureLayout = (ConstraintLayout)viewInflater.inflate(R.layout.home_delete_lecture_dialog, null);

        // dialog builder ; deleteLectureAlertBuiler
        final AlertDialog.Builder deleteLectureAlertBuilder = new AlertDialog.Builder(this);
        deleteLectureAlertBuilder.setTitle("강의 삭제하기");
        deleteLectureAlertBuilder.setView(deleteLectureLayout);
        deleteLectureAlertBuilder.setPositiveButton("강의 삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int currentLectureIndex = 0;
                int numOfLectures = lectureBundle.get(0).size();
                for (int lectureIndex = 0; lectureIndex < numOfLectures; lectureIndex++) {
                    if (lectureBundle.get(0).get(lectureIndex).getLectureId() == lectureId) {
                        currentLectureIndex = lectureIndex;
                    }
                }
                Lecture_List_Item lectureListItemToDelete = lectureBundle.get(0).get(currentLectureIndex);

                ArrayList<ClassInfo> classInfoArrayList = classInfoParser(lectureListItemToDelete);
                int numOfClassDate = classInfoArrayList.size();
                for (int index = 0; index < numOfClassDate; index++) {
                    int dateIndex = classInfoArrayList.get(index).getDayInt();
                    timeTableDateRelativeLayouts[dateIndex].removeView(lectureBlockTextViews[dateIndex][startTimeBlockIndex]);
                }
                updateLectureToDB(lectureListItemToDelete.getLectureId(), "DEL");
                // 스케쥴에서도 삭제해준다.
                schedule.deleteSchedule(lectureTimeBlocksString);
                Toast.makeText(getApplicationContext(),
                        "" + lectureName + " 강의를 삭제하였습니다." + currentLectureIndex,
                        Toast.LENGTH_SHORT).show();
            }
        });

        deleteLectureAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });

        final AlertDialog deleteLectureDialog = deleteLectureAlertBuilder.create();
        deleteLectureDialog.show();

    }

    // show Dialog to add lectures.
    private void showAddLectureDialog() {
        // initialization
        lectureListItemToAdd = null;

        LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout addLectureLayout = (ConstraintLayout)viewInflater.inflate(R.layout.home_add_lecture_dialog, null);

        final EditText searchEditText = (EditText)addLectureLayout.findViewById(R.id.lectureSearchBarEditText);
        final ImageButton imgbtnLectureSearch = (ImageButton)addLectureLayout.findViewById(R.id.imgbtnLectureSearch);
        final ImageButton imgbtnLectureClickSearch = (ImageButton)addLectureLayout.findViewById(R.id.imgbtnLectureClickSearch);

        final View homeAddLectureSearchBarOriginBlock = (View)addLectureLayout.findViewById(R.id.home_add_lecture_search_bar_origin_block);
        final View homeAddLectureSearchBarClickSearchBlock = (View)addLectureLayout.findViewById(R.id.home_add_lecture_search_bar_click_search_block);

        final Spinner majorSpinner = (Spinner)addLectureLayout.findViewById(R.id.majorSpinner);
        final ListView listView = (ListView)addLectureLayout.findViewById(R.id.lectureListView);

        // setting search bar buttons
        imgbtnLectureSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeAddLectureSearchBarOriginBlock.setVisibility(View.GONE);
                homeAddLectureSearchBarClickSearchBlock.setVisibility(View.VISIBLE);
            }
        });
        imgbtnLectureClickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeAddLectureSearchBarClickSearchBlock.setVisibility(View.GONE);
                homeAddLectureSearchBarOriginBlock.setVisibility(View.VISIBLE);
                int lengthOfAllLectures = lectureBundle.get(0).size();
                ArrayList<Lecture_List_Item> lectureListItemsSearched = new ArrayList<>();
                for (int index = 0; index < lengthOfAllLectures; index++) {
                    Lecture_List_Item oneOfEachLectures = lectureBundle.get(0).get(index);
                    if (oneOfEachLectures.getLectureName().contains(searchEditText.getText())) {
                        lectureListItemsSearched.add(oneOfEachLectures);
                    }
                }
                ListAdapter searchListAdapter = new ListAdapter(HomeActivity.this, lectureListItemsSearched);
                listView.setAdapter(searchListAdapter);
                searchEditText.setText("");
            }
        });

        // setting spinner adapter
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.major, R.layout.home_major_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.home_major_spinner_item);
        majorSpinner.setAdapter(spinnerAdapter);

        // setting listView adapter
        final ArrayList<ListAdapter> listAdapterBundle = new ArrayList<ListAdapter>();

        // server 로부터 강의 불러온 후 수강신청하기
        for (int index = 0; index < numOfMajor; index++) {
            listAdapterBundle.add(new ListAdapter(this, lectureBundle.get(index)));
        }

        listView.setAdapter(listAdapterBundle.get(0));
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // list 를 클릭할 경우
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                Lecture_List_Item lectureClicked = (Lecture_List_Item)adapterView.getItemAtPosition(i);
                // 강의 선택 -> 임시 강의 변수에 저장한다.
                lectureListItemToAdd = lectureClicked;
            }
        });

        // spinner listener
        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                listView.setAdapter(listAdapterBundle.get(majorSpinner.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                lectureListItemToAdd = null; // 추가하려고 임시저장한 데이터를 비운다
            }
        });

        // dialog builder ; addLectureAlertBuilder
        final AlertDialog.Builder addLectureAlertBuilder = new AlertDialog.Builder(this);
        addLectureAlertBuilder.setTitle("강의 추가하기");
        addLectureAlertBuilder.setView(addLectureLayout);
        addLectureAlertBuilder.setPositiveButton("강의 추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                // do nothing here
            }
        });

        addLectureAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });


        final AlertDialog addLectureDialog = addLectureAlertBuilder.create(); // 강의 추가 버튼 overriding
        addLectureDialog.show();
        addLectureDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lectureListItemToAdd == null) {
                    Toast.makeText(getBaseContext(), "강의를 선택하세요", Toast.LENGTH_SHORT).show();
                } else {
                    int lectureId = lectureListItemToAdd.getLectureId();
                    String lectureName = lectureListItemToAdd.getLectureName();
                    String profName = lectureListItemToAdd.getProfName();
                    ArrayList<ClassInfo> classInfoArrayList = classInfoParser(lectureListItemToAdd);

                    int numOfClassDate = classInfoArrayList.size(); // 일주일에 강의가 몇개의 요일에 있는지
                    String lectureTimeBlocksString = getLectureTimeBlockString(numOfClassDate, classInfoArrayList); // 강의의 요일별 시간을 string 으로 저장

                    boolean validate = false; // 시간표 겹치는 여부 검증
                    validate = schedule.validateSchedule(lectureTimeBlocksString);
                    if (validate) { // 겹치지 않을때 스케쥴에 넣어주고 시간표에 강의를 그린다
                        int numOfParties = partiesOnTimeTables.size();
                        for (int index = 0; index < numOfParties; index++) {
                            Party partyToDelete = partiesOnTimeTables.get(index);
                            int startHour = partyToDelete.getStartHour();
                            int startMiute = partyToDelete.getStartMinute();
                            int dateIndex = partyToDelete.getDayInt() - 2;
                            int startTimeBlockIndex = (startHour - 9) * 2 + startMiute / 30;
                            timeTableDateRelativeLayouts[dateIndex].removeView(partyBlockTextViews[dateIndex][startTimeBlockIndex]);
                        }
                        schedule.addSchedule(lectureTimeBlocksString);
                        drawLectureOnTimeTable(lectureListItemToAdd, classInfoArrayList, lectureId, numOfClassDate, lectureName, profName);
                        updateLectureToDB(lectureId, "ADD");
                        for (int index = 0; index < numOfParties; index++) {
                            drawPartyOnTimeTable(partiesOnTimeTables.get(index));
                        }

                        addLectureDialog.dismiss();
                    } else {
                        Toast.makeText(HomeActivity.this,
                                "강의가 겹칩니다. 추가할 수 없습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    // initialize lecture array list
    public void initLectureBundle(int numOfMajor) {
        for (int index = 0; index < numOfMajor; index++) {
            lectureBundle.add(new ArrayList<Lecture_List_Item>());
        }
    }

    // 요일별 강의를 표시해줄 relative layout을 초기화 해준다.
    public void initTableDateRelativeLayouts() {
        timeTableDateRelativeLayouts[0] = (RelativeLayout) findViewById(R.id.mondayRelativeLayout);
        timeTableDateRelativeLayouts[1] = (RelativeLayout) findViewById(R.id.tuesdayRelativeLayout);
        timeTableDateRelativeLayouts[2] = (RelativeLayout) findViewById(R.id.wednesdayRelativeLayout);
        timeTableDateRelativeLayouts[3] = (RelativeLayout) findViewById(R.id.thursdayRelativeLayout);
        timeTableDateRelativeLayouts[4] = (RelativeLayout) findViewById(R.id.fridayRelativeLayout);
    }

    // 서버로부터 user id 에 따른 수강 목록을 불러온다.
    private void getLecturesFromDB() {
        int userId = Account.getInstance().getUserId();

        NetworkService networkService = App.retrofit.create(NetworkService.class);
        final Call<List<Lecture>> lectureListRequestCall = networkService.getAllLectures(userId);

        lectureListRequestCall.enqueue(new Callback<List<Lecture>>() {

            @Override
            public void onResponse(Response<List<Lecture>> response, Retrofit retrofit) {
                List<Lecture> lectureRequestResult = response.body(); // 전체 강의 목록을 받아옴

                // 서버로의 데이터를 전체 강의 리스트에 저장한다.
                for (int index = 0; index < lectureRequestResult.size(); index++) {
                    try {
                        JSONObject jsonObject = new JSONObject(lectureRequestResult.get(index).getLecture_info());
                        int id = lectureRequestResult.get(index).getId();
                        String major = jsonObject.getString("major");
                        String profName = jsonObject.getString("prof_name");
                        String lectureName = jsonObject.getString("lecture_name");
                        String classInfo = jsonObject.getString("class_info");
                        Lecture_List_Item tempLectureItem = new Lecture_List_Item(id, major, profName, classInfo, lectureName);
                        lectureBundle.get(0).add(tempLectureItem);
                        // TODO lecture bundle 에 강의 하나씩 add 하는 곳
                        switch (major) {
                            case "소프트웨어학과" :
                                lectureBundle.get(1).add(tempLectureItem);
                                break;
                            case "사이버보안학과" :
                                lectureBundle.get(2).add(tempLectureItem);
                                break;
                            case "국방디지털융합학과" :
                                lectureBundle.get(3).add(tempLectureItem);
                                break;
                            case "교양" :
                                lectureBundle.get(4).add(tempLectureItem);
                                break;
                            default:
                                Toast.makeText(getBaseContext(),
                                        "major : " + major + "이 학과 목록에 없습니다.",
                                        Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getLectureIdsFromDB(); // currentLectures에 수강하는 강의들이 저장됌
                getPartiesFromDB();
            }


            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러 : " + t.getMessage());
            }
        });
    }

    // user가 현재 수강하고 있는 강의의 id를 저장하고 있는 list를 받아온다
    public void getLectureIdsFromDB() {
        int userId = Account.getInstance().getUserId();

        NetworkService networkService = App.retrofit.create(NetworkService.class);
        final Call<List<Integer>> lectureIdsRequestCall = networkService.getUserLectureIds(userId);

        lectureIdsRequestCall.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Response<List<Integer>> response, Retrofit retrofit) {
                List<Integer> lectureIdsResult = response.body();
                if (!isDrawedFromDB) {
                    isDrawedFromDB = true;
                    for (int index = 0; index < lectureIdsResult.size(); index++) {
                        int currentLectureId = lectureIdsResult.get(index);
                        int currentLectureIndex = 0;
                        int numOfLectures = lectureBundle.get(0).size();
                        for (int lectureIndex = 0; lectureIndex < numOfLectures; lectureIndex++) {
                            if (lectureBundle.get(0).get(lectureIndex).getLectureId() == currentLectureId) {
                                currentLectureIndex = lectureIndex;
                            }
                        }
                        Lecture_List_Item currentLectureListItem = lectureBundle.get(0).get(currentLectureIndex);
                        ArrayList<ClassInfo> classInfoArrayList = classInfoParser(currentLectureListItem);
                        int numOfClassDate = classInfoArrayList.size();
                        String lectureName = currentLectureListItem.getLectureName();
                        String profName = currentLectureListItem.getProfName();
                        String lectureTimeBlocksString = getLectureTimeBlockString(numOfClassDate, classInfoArrayList);

                        schedule.addSchedule(lectureTimeBlocksString);
                        drawLectureOnTimeTable(currentLectureListItem, classInfoArrayList, currentLectureId, numOfClassDate, lectureName, profName);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러 : " + t.getMessage());
            }
        });
    }

    // 강의를 추가하거나 삭제하는 것을 서버에 update 한다.
    public void updateLectureToDB(int lectureId, final String flag) {
        int userId = Account.getInstance().getUserId();

        NetworkService networkService = App.retrofit.create(NetworkService.class);
        final Call<UpdateResult> updateLectureCall = networkService.updateLecture(userId, lectureId, flag);

        updateLectureCall.enqueue(new Callback<UpdateResult>() {
            @Override
            public void onResponse(Response<UpdateResult> response, Retrofit retrofit) {
                UpdateResult updateResult = response.body();
                if (updateResult.strReceiveMessage.equals("success")) {
                    Toast.makeText(getBaseContext(), "강의가 " + flag + " 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "강의 " + flag + "에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러 : " + t.getMessage());
            }
        });
    }

    // user가 현재 참여하고 있는 파티의 정보를 모두 가져온다.
    public void getPartiesFromDB() {
        int userId = Account.getInstance().getUserId();

        NetworkService networkService = App.retrofit.create(NetworkService.class);
        final Call<ArrayList<PartyInDatabase>> partyRequestCall = networkService.getUserParties(userId);

        partyRequestCall.enqueue(new Callback<ArrayList<PartyInDatabase>>() {
            @Override
            public void onResponse(Response<ArrayList<PartyInDatabase>> response, Retrofit retrofit) {
                partyArrayList.clear();
                ArrayList<PartyInDatabase> partyInDatabaseArrayList = response.body(); // 전체 파티 목록을 받아옴
                for(PartyInDatabase partyInDatabase : partyInDatabaseArrayList) {
                    partyArrayList.add(new Party(partyInDatabase));
                }
                for (int index = 0; index < partyArrayList.size(); index++) {
                    drawPartyOnTimeTable(partyArrayList.get(index)); // 파티를 그린다.
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러 : " + t.getMessage());
            }
        });
    }

    // lecture list item 을 class info 로 parsing
    public ArrayList<ClassInfo> classInfoParser(Lecture_List_Item lecture_list_item) {

        String classInfoOrig = lecture_list_item.getClassInfo();
        int jsonArrayLen = classInfoOrig.length() - classInfoOrig.replace("{", "").length();
        ArrayList<ClassInfo> classInfoArrayList = new ArrayList<>(jsonArrayLen);
        try {
            JSONArray jsonArray = new JSONArray(classInfoOrig);
            for(int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ClassInfo tempClassInfo = new ClassInfo(ClassInfo.Day.MON, 0, 0, "");
                tempClassInfo.setClassDay(jsonObject.getString("day"));
                tempClassInfo.setClassStartTimeByMinute(Integer.parseInt(jsonObject.getString("start_time")));
                tempClassInfo.setClassEndTimeByMinute(Integer.parseInt(jsonObject.getString("end_time")));
                tempClassInfo.setClassPlace(jsonObject.getString("place"));

                classInfoArrayList.add(tempClassInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classInfoArrayList;
    }

    // lecture 를 timetable 위에 그려준다.
    public void drawLectureOnTimeTable(Lecture_List_Item lecture, ArrayList<ClassInfo>classInfoArrayList, int lectureId, int numOfClassDate, String lectureName, String profName) {
        for (int dateIndex = 0; dateIndex < numOfClassDate; dateIndex++) {
            int blockDuration = classInfoArrayList.get(dateIndex).getDurationMinute() / 30;
            int startHour = classInfoArrayList.get(dateIndex).getStartHour();
            int startMinute = classInfoArrayList.get(dateIndex).getStartMinute();
            ClassInfo.Day date = classInfoArrayList.get(dateIndex).getDay();
            int startTimeBlockIndex = (startHour - 9) * 2 + startMinute / 30;

            String lectureTimeBlocksString = getLectureTimeBlockString(numOfClassDate, classInfoArrayList);

            switch (date) {
                case MON:
                    drawLectureBlockandSave(lecture, 0, lectureId, blockDuration, startTimeBlockIndex, lectureName, profName, lectureTimeBlocksString);
                    break;
                case TUE:
                    drawLectureBlockandSave(lecture, 1, lectureId,  blockDuration, startTimeBlockIndex, lectureName, profName, lectureTimeBlocksString);
                    break;
                case WED:
                    drawLectureBlockandSave(lecture, 2, lectureId,  blockDuration, startTimeBlockIndex, lectureName, profName, lectureTimeBlocksString);
                    break;
                case THU:
                    drawLectureBlockandSave(lecture, 3, lectureId,  blockDuration, startTimeBlockIndex, lectureName, profName, lectureTimeBlocksString);
                    break;
                case FRI:
                    drawLectureBlockandSave(lecture, 4, lectureId,  blockDuration, startTimeBlockIndex, lectureName, profName, lectureTimeBlocksString);
                    break;
            }

        }

    }

    // 각 lecture의 timeblock을 요일에 맞춰서 그려준다.
    public void drawLectureBlockandSave(final Lecture_List_Item lecture, final int dateIndex, final int lectureId, int blockDuration, final int startTimeBlockIndex, final String lectureName, String profName, final String lectureTimeBlocksString) {
        final AutoResizeTextView currentLectureTextView = new AutoResizeTextView(getApplicationContext());
        currentLectureTextView.setText("" + lectureName + "(" + profName + ")");
        currentLectureTextView.setTextSize(12);
        GradientDrawable roundedCorner = new GradientDrawable();
        roundedCorner.setCornerRadius( 12 );
        roundedCorner.setColor(Color.parseColor("#EDEDED"));
        currentLectureTextView.setBackground(roundedCorner);
        currentLectureTextView.setGravity(Gravity.CENTER);
        currentLectureTextView.setHeight(blockDuration*timeTableBlockHeight);
        currentLectureTextView.setTextColor(Color.BLACK);
        RelativeLayout.LayoutParams currentRelativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        currentRelativeLayoutParams.setMargins(8, startTimeBlockIndex*timeTableBlockHeight, 8, 8);
        currentLectureTextView.setLayoutParams(currentRelativeLayoutParams);
        timeTableDateRelativeLayouts[dateIndex].addView(currentLectureTextView);
        currentLectureTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDelelteLectureDialog(lectureId, startTimeBlockIndex, lectureName, lectureTimeBlocksString);
                return false;
            }
        });
        currentLectureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLectureInfoDialog(lecture);
            }
        });
        for (int block = startTimeBlockIndex; block < startTimeBlockIndex + blockDuration; block++) {
            lectureBlockTextViews[dateIndex][block] = currentLectureTextView; // 그려져 있다는 것을 기억하기 위해서 저장한다.
        }
    }

    // party 를 timetable 위에 그려준다.
    public void drawPartyOnTimeTable(final Party party) {
        int blockDuration = party.getDurationMunute() / 30;
        int startHour = party.getStartHour();
        int startMiute = party.getStartMinute();
        int intDate = party.getDayInt();

        if (startHour < 9 || startHour > 21) {
            return;
        } else if (blockDuration <= 0) {
            return;
        }

        partiesOnTimeTables.add(party);

        int dateIndex = intDate - 2;
        int startTimeBlockIndex = (startHour - 9) * 2 + startMiute / 30;
        final AutoResizeTextView currentPartyTextView = new AutoResizeTextView(getApplicationContext());
        currentPartyTextView.setText("" + party.getPartyName());
        currentPartyTextView.resetTextSize();
        currentPartyTextView.setTextSize(12);
        String colorValue = timeTableCellColor.getColorValues(party.getPartyId());
        currentPartyTextView.setBackgroundColor(Color.parseColor(colorValue));
        currentPartyTextView.setGravity(Gravity.CENTER);
        currentPartyTextView.setHeight(blockDuration*timeTableBlockHeight);
        currentPartyTextView.setTextColor(Color.BLACK);
        RelativeLayout.LayoutParams currentRelativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        currentRelativeLayoutParams.setMargins(12, startTimeBlockIndex*timeTableBlockHeight, 12, 8);
        currentPartyTextView.setLayoutParams(currentRelativeLayoutParams);
        timeTableDateRelativeLayouts[dateIndex].addView(currentPartyTextView);
        currentPartyTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showMoveToChattingDialog(party);
                return false;
            }
        });
        currentPartyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPartyInfoDialog(party);
            }
        });
        for (int block = startTimeBlockIndex; block < startTimeBlockIndex + blockDuration; block++) {
            partyBlockTextViews[dateIndex][block] = currentPartyTextView;
        }
    }

    // schedule 테스트 String 생성기
    public String getLectureTimeBlockString(int numOfClassDate, ArrayList<ClassInfo> classInfoArrayList) {
        String lectureTimeBlocksString = "";

        for (int dateIndex = 0; dateIndex < numOfClassDate; dateIndex++) {
            int blockDuration = classInfoArrayList.get(dateIndex).getDurationMinute() / 30;
            int startHour = classInfoArrayList.get(dateIndex).getStartHour();
            int startMinute = classInfoArrayList.get(dateIndex).getStartMinute();
            ClassInfo.Day date = classInfoArrayList.get(dateIndex).getDay();
            int startTimeBlockIndex = (startHour - 9) * 2 + startMinute / 30;
            lectureTimeBlocksString += classInfoArrayList.get(dateIndex).getDayString() + ":";
            for (int block = startTimeBlockIndex; block < startTimeBlockIndex + blockDuration; block++) {
                lectureTimeBlocksString += "[" + String.valueOf(block) + "]";
            }
        }
        return lectureTimeBlocksString;
    }

    // update to server 결과 수신용 class
    public class UpdateResult{
        String strReceiveMessage;
    }

}