package xyz.capsaicine.freeperiod.activities.home;


//TODO 28 -> 26

// 겹치는 강의를 처리해 주기 위한 클래스
public class Schedule {
    private String monday[] = new String[26]; // 8시 ~ 21시 30분 단위 14*2 28개
    private String tuesday[] = new String[26];
    private String wednesday[] = new String[26];
    private String thursday[] = new String[26];
    private String friday[] = new String[26];

//    constructor
    public Schedule() { // 요일 별 28개의 timeblock
        for(int timeIndex = 0; timeIndex < 26; timeIndex++) { // initialize
            monday[timeIndex] = "";
            tuesday[timeIndex] = "";
            wednesday[timeIndex] = "";
            thursday[timeIndex] = "";
            friday[timeIndex] = "";
        }
    }

    public void addSchedule(String scheduleText) {
        int indexContainer;
        if((indexContainer = scheduleText.indexOf("MON")) > -1) { // "MON"이라는 단어가 포함 되면 그 위치를 저장한다.
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    monday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "수업";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("TUE")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    tuesday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "수업";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("WED")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    wednesday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "수업";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("THU")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    thursday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "수업";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("FRI")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    friday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "수업";
                }
            }
        }

    }


    public void deleteSchedule(String scheduleText) {
        int indexContainer;
        if((indexContainer = scheduleText.indexOf("MON")) > -1) { // "MON"이라는 단어가 포함 되면 그 위치를 저장한다.
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    monday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("TUE")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    tuesday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("WED")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    wednesday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("THU")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    thursday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "";
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("FRI")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    friday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "";
                }
            }
        }

    }

    public boolean validateSchedule(String scheduleText) {
        if(scheduleText.equals("")) { // 비어있는 경우
            return true;
        }
        int indexContainer;
        if((indexContainer = scheduleText.indexOf("MON")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    if(!monday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))].equals("")) { // 현재 값이 공백이 아니라면, 즉 이미 수업이 있다면,
                        return false; // 스케쥴이 겹친다.
                    }
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("TUE")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    if(!tuesday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))].equals("")) {
                        return false;
                    }
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("WED")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    if(!wednesday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))].equals("")) {
                        return false;
                    }
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("THU")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    if(!thursday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))].equals("")) {
                        return false;
                    }
                }
            }
        }
        if((indexContainer = scheduleText.indexOf("FRI")) > -1) {
            indexContainer +=4; // 데이터를 파싱하기 위해 데이터 index로 이동
            int startPoint = indexContainer;
            int endPoint = indexContainer;
            for(int i = indexContainer; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                if(scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }
                if(scheduleText.charAt(i) == ']') {
                    endPoint = i;
                    if(!friday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))].equals("")) {
                        return false;
                    }
                }
            }
        }
        return true; // 모두 해당되지 않으면 스케쥴이 겹치지 않는 것이다.
    }

}
