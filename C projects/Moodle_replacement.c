#include <stdio.h>
#include <string.h>
#include <malloc.h>
/*Defining ASCII indices for implementing string content check function.*/
#define A_ind 65
#define Z_ind 90
#define a_ind 97
#define z_ind 122

enum ExamType{
    WRITTEN,
    DIGITAL
};

union ExamInfo{
    short duration;
    char software[20];
};

struct Student{
    short student_id;
    char name[20];
    char faculty[30];
};

struct Exam{
    short exam_id;
    int exam_type;
    union ExamInfo exam_info;
};

struct Exam_Grade{
    short exam_id;
    short student_id;
    short grade;
};

/*Here we declare and initialize arrays of Students, Exams and Grades and counters as global variables.
Counters are used to store indices of the last used array elements. If an element was deleted,
vacated place will be never reused.*/
struct Student Students[1000];
struct Exam Exams[500];
struct Exam_Grade Grades[500000];
int std_counter = 0, ex_counter = 0, gr_counter = 0;

/*This function checks if a string contains any symbols, exept upper and lower case English letters.
It returns 0 if there are any, and 1 if there aren't.*/
int cont_check(char str[]){
    for (int i = 0; i < strlen(str); i++){
        if (!(((A_ind<=str[i])&&(str[i]<=Z_ind))||((a_ind<=str[i])&&(str[i]<=z_ind)))){
            return 0;
        }
    }
    return 1;
}

/*This function processes a query and chooses an appropriate function to execute it.
It takes a string containing query and pointers to input and output files.*/
void Choose(char* query, FILE* input, FILE* output){
    if (!(strcmp(query, "ADD_STUDENT"))) Add_Std(input, output);
    else if (!(strcmp(query, "ADD_EXAM"))) Add_Ex(input, output);
    else if (!(strcmp(query, "ADD_GRADE"))) Add_Gr(input, output);
    else if (!(strcmp(query, "UPDATE_EXAM"))) Upd_Ex(input, output);
    else if (!(strcmp(query, "UPDATE_GRADE"))) Upd_Gr(input, output);
    else if (!(strcmp(query, "SEARCH_STUDENT"))) Srch_Std(input, output);
    else if (!(strcmp(query, "SEARCH_GRADE"))) Srch_Gr(input, output);
    else if (!(strcmp(query, "DELETE_STUDENT"))) Del_Std(input, output);
    else if (!(strcmp(query, "LIST_ALL_STUDENTS"))) List_All(output);
    else if (!(strcmp(query, "END"))) return;
}


/*This function adds new student to the array. It consequentially scans arguments from the input file
and checks if they satisfy the requirements. If everything is okay, it copies the information to the fields
of the first array element that was never used before and prints success report to the output file.
If not, in prints an apropriate error report to the ouput file.*/
void Add_Std(FILE* input, FILE* output){
    int id;
    char name[40], fac[50];
    fscanf(input, "%d", &id);
    fscanf(input, "%s", &name);
    fscanf(input, "%s", &fac);
    for (int i = 0; i < std_counter; i++){
        if (Students[i].student_id == id){
            fprintf(output, "Student: %d already exists\n", id);
            return;
        }
    }
    if (!((0<id)&&(id<1000))){
        fprintf(output, "Invalid student id\n");
        return;
    }
    if ((!((1<strlen(name))&&(strlen(name)<20)))||(!(cont_check(name)))){
        fprintf(output, "Invalid name\n");
        return;
    }
    if ((!((4<strlen(fac))&&(strlen(fac)<30)))||(!(cont_check(fac)))){
        fprintf(output, "Invalid faculty\n");
        return;
    }

    Students[std_counter].student_id = id;
    memcpy(Students[std_counter].name, name, sizeof(name));
    memcpy(Students[std_counter].faculty, fac, sizeof(fac));
    fprintf(output, "Student: %d added\n", id);
    ++std_counter;
    return;
}


/*This function adds new exam. It does same things as previous one. Before scanning exam info,
 function checks if the exam is written or digital to identify data type of exam info.*/
void Add_Ex (FILE* input, FILE* output){
    int id, enum_type;
    char type[15];
    union ExamInfo info;
    fscanf(input, "%d", &id);

    for (int i = 0; i < ex_counter; i++){
        if (Exams[i].exam_id == id){
            fprintf(output, "Exam: %d already exists\n", id);
            return;
        }
    }

    if (!((0<id)&&(id<500))){
        fprintf(output, "Invalid exam id\n");
        return;
    }

    fscanf(input, "%s", &type);
    if (!(strcmp(type, "WRITTEN"))){
        enum_type = WRITTEN;
        fscanf(input, "%d", &info.duration);
        if (!((40<=info.duration)&&(info.duration<=180))){
            fprintf(output, "Invalid duration\n");
            return;
        }
    }
    else if (!(strcmp(type, "DIGITAL"))){
        enum_type = DIGITAL;
        fscanf(input, "%s", &info.software);
        if ((!((2<strlen(info.software))&&(strlen(info.software)<20)))||(!(cont_check(info.software)))){
            fprintf(output, "Invalid software\n");
            return;
        }
    }
    else{
        fprintf(output, "Invalid exam type\n");
        return;
    }

    Exams[ex_counter].exam_id = id;
    Exams[ex_counter].exam_type = enum_type;
    Exams[ex_counter].exam_info = info;
    fprintf(output, "Exam: %d added\n", id);
    ++ex_counter;
    return;
}


/*This function adds a new grade. It does the same things as the add student function.*/
void Add_Gr(FILE* input, FILE* output){
    int std_id, ex_id, grade;
    fscanf(input, "%d", &ex_id);
    fscanf(input, "%d", &std_id);
    fscanf(input, "%d", &grade);
    int ex_exist = 0, std_exist = 0;

    for (int i = 0; i < ex_counter; i++){
        if (Exams[i].exam_id == ex_id){
            ++ex_exist;
            break;
        }
    }
    if (ex_exist == 0){
        fprintf(output, "Exam not found\n");
        return;
    }

    if (!((0<ex_id)&&(ex_id<500))){
        fprintf(output, "Invalid exam id\n");
        return;
    }

    for (int i = 0; i < std_counter; i++){
        if (Students[i].student_id == std_id){
            ++std_exist;
            break;
        }
    }
    if (std_exist == 0){
        fprintf(output, "Student not found\n");
        return;
    }

    if (!((0<std_id)&&(std_id<1000))){
        fprintf(output, "Invalid student id\n");
        return;
    }

    if (!((0<=grade)&&(grade<=100))){
        fprintf(output, "Invalid grade\n");
        return;
    }

    Grades[gr_counter].exam_id = ex_id;
    Grades[gr_counter].student_id = std_id;
    Grades[gr_counter].grade = grade;
    fprintf(output, "Grade %d added for the student: %d\n", grade, std_id);
    ++gr_counter;
    return;
}


/*This function searches a student by its id among the ones added earlier. It checks if the student is found
and if the entered id satisfies the requirements. If everything is okay, the function prints all the information
about student to the output file.*/
void Srch_Std(FILE* input, FILE* output){
    int id;
    fscanf(input, "%d", &id);
    int std_ind = -1;

    for(int i = 0; i < std_counter; i++){
        if (Students[i].student_id == id){
            std_ind = i;
            break;
        }
    }
    if (std_ind == -1){
        fprintf(output, "Student not found\n");
        return;
    }

    if (!((0<id)&&(id<1000))){
        fprintf(output, "Invalid student id\n");
        return;
    }

    fprintf(output, "ID: %d, Name: %s, Faculty: %s\n", Students[std_ind].student_id,
            Students[std_ind].name, Students[std_ind].faculty);
    return;
}


/*This function searches grades by student and exam id. It checks the inputs
and prints student's id, name, grade and all the information about the exam to the output file.*/
void Srch_Gr(FILE* input, FILE* output){
    int ex_id, std_id;
    fscanf(input, "%d", &ex_id);
    fscanf(input, "%d", &std_id);
    int ex_ind = -1, std_ind = -1, gr_ind;

    for (int i = 0; i < ex_counter; i++){
        if (Exams[i].exam_id == ex_id){
            ex_ind = i;
            break;
        }
    }
    if (ex_ind == -1){
        fprintf(output, "Exam not found\n");
        return;
    }

    if (!((0<ex_id)&&(ex_id<500))){
        fprintf(output, "Invalid exam id\n");
        return;
    }

    for(int i = 0; i < std_counter; i++){
        if (Students[i].student_id == std_id){
            std_ind = i;
            break;
        }
    }
    if (std_ind == -1){
        fprintf(output, "Student not found\n");
        return;
    }

    if (!((0<std_id)&&(std_id<1000))){
        fprintf(output, "Invalid student id\n");
        return;
    }

    for (int i = 0; i < gr_counter; i++){
        if ((Grades[i].exam_id == ex_id)&&(Grades[i].student_id == std_id)) gr_ind = i;
    }
    fprintf(output, "Exam: %d, Student: %d, Name: %s, Grade: %d, ",
            Exams[ex_ind].exam_id, Students[std_ind].student_id, Students[std_ind].name, Grades[gr_ind].grade);
    if (Exams[ex_ind].exam_type == WRITTEN){
        fprintf(output, "Type: WRITTEN, Info: %d\n", Exams[ex_ind].exam_info.duration);
    }
    else fprintf(output, "Type: DIGITAL, Info: %s\n", Exams[ex_ind].exam_info.software);
    return;
}


/*This function searches the exam by its id and updates its type and information. It checks the inputs
and replaces the old exam data with the new one.*/
void Upd_Ex(FILE* input, FILE* output){
    int id;
    fscanf(input, "%d", &id);
    int ex_ind;
    for (int i = 0; i < ex_counter; i++){
        if (Exams[i].exam_id == id){
            ex_ind = i;
            break;
        }
    }
    
    char type[15];
    union ExamInfo info;
    int enum_type;
    fscanf(input, "%s", &type);
    if (!(strcmp(type, "WRITTEN"))){
        enum_type = WRITTEN;
        fscanf(input, "%d", &info.duration);
        if (!((40<=info.duration)&&(info.duration<=180))){
            fprintf(output, "Invalid duration\n");
            return;
        }
    }
    else if (!(strcmp(type, "DIGITAL"))){
        enum_type = DIGITAL;
        fscanf(input, "%s", &info.software);
        if (!((2<strlen(info.software))&&(strlen(info.software)<20))){
            fprintf(output, "Invalid software\n");
            return;
        }
    }
    else{
        fprintf(output, "Invalid exam type\n");
        return;
    }

    Exams[ex_ind].exam_type = enum_type;
    Exams[ex_ind].exam_info = info;
    fprintf(output, "Exam: %d updated\n", id);
    return;
}


/*This function searches the grade by student and exam ids and updates it. It checks if the grade satisfies
the requirements and replaces the old grade with the new one, if everything is okay.*/
void Upd_Gr(FILE* input, FILE* output){
    int std_id, ex_id, grade;
    fscanf(input, "%d", &ex_id);
    fscanf(input, "%d", &std_id);
    fscanf(input, "%d", &grade);
    int gr_ind;
    for (int i = 0; i < gr_counter; i++){
        if ((Grades[i].exam_id == ex_id)&&(Grades[i].student_id == std_id)) gr_ind = i;
    }

    if (!((0<=grade)&&(grade<=100))){
        fprintf(output, "Invalid grade\n");
        return;
    }

    Grades[gr_ind].grade = grade;
    fprintf(output, "Grade %d updated for the student: %d\n", grade, std_id);
    return;
}


/*This function searches the student by his id and deletes the information about him and his grades.
As the vacated elements are not reused, the deleted data is replaced with -1, if it is numeric,
and with empty string, if it is string. In this way I indicate that the element was used already.*/
void Del_Std(FILE* input, FILE* output){
    int id;
    fscanf(input, "%d", &id);
    for (int i = 0; i < std_counter; i++){
        if (Students[i].student_id == id){
            Students[i].student_id = -1;
            memcpy(Students[i].name, "", sizeof(char));
            memcpy(Students[i].faculty, "", sizeof(char));
        }
    }
    for (int i=0; i < gr_counter; i++){
        if (Grades[i].student_id == id){
            Grades[i].student_id = -1;
            Grades[i].exam_id = -1;
            Grades[i].grade = -1;
        }
    }
    fprintf(output, "Student: %d deleted\n", id);
    return;
}


/*This function prints the information about all students that were added earlier and were not deleted.
The information is printed in the order the students have been added.*/
void List_All(FILE* output){
    for (int i = 0; i < std_counter; i++){
        if (Students[i].student_id != -1){
            fprintf(output, "ID: %d, Name: %s, Faculty: %s\n",
                    Students[i].student_id, Students[i].name, Students[i].faculty);
        }
    }
    return;
}




int main(){
    /*Declaring and initializing variable for storing queries and opening input and output files.*/
    char query[50];
    FILE* input = fopen("input.txt", "r");
    FILE* output = fopen("output.txt", "w");
    /*Сonsequentially scanning queries and calling Choose function for eeach one.
    The cycle ends when the scanned query is END.*/
    while (strcmp(query, "END")){
        fscanf(input, "%s", &query);
        Choose(query, input, output);
    }
    return 0;
}