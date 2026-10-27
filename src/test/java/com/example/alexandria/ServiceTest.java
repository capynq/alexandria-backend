package com.example.alexandria;

import com.example.alexandria.repository.AnnouncementRepository;
import com.example.alexandria.repository.GroupRepository;
import com.example.alexandria.repository.HomeworkRepository;
import com.example.alexandria.repository.HomeworkStatusRepository;
import com.example.alexandria.repository.ScheduleRepository;
import com.example.alexandria.repository.StudentRepository;
import com.example.alexandria.repository.SubjectRepository;
import com.example.alexandria.repository.TeacherRepository;
import com.example.alexandria.repository.entity.Announcement;
import com.example.alexandria.repository.entity.Group;
import com.example.alexandria.repository.entity.Homework;
import com.example.alexandria.repository.entity.HomeworkStatus;
import com.example.alexandria.repository.entity.Schedule;
import com.example.alexandria.repository.entity.Student;
import com.example.alexandria.repository.entity.Subject;
import com.example.alexandria.repository.entity.Teacher;
import com.example.alexandria.security.PasswordUtil;
import com.example.alexandria.service.AnnouncementService;
import com.example.alexandria.service.GroupService;
import com.example.alexandria.service.HomeworkService;
import com.example.alexandria.service.HomeworkStatusService;
import com.example.alexandria.service.ScheduleService;
import com.example.alexandria.service.StudentService;
import com.example.alexandria.service.SubjectService;
import com.example.alexandria.service.TeacherService;
import com.example.alexandria.service.dto.StudentDTO;
import com.example.alexandria.service.dto.AnnouncementDTO;
import com.example.alexandria.service.dto.ScheduleDTO;
import com.example.alexandria.service.dto.SubjectDTO;
import com.example.alexandria.service.dto.TeacherDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceTest {
    @Test
    void announcementCrudAndValidation() {
        AnnouncementRepository repo = mock(AnnouncementRepository.class);
        AnnouncementService service = new AnnouncementService(repo);
        Announcement entity = Announcement.builder().id(1).title("Old").content("Body").authorId(Teacher.builder().id(2).build()).build();
        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(repo.findAnnouncementById(1L)).thenReturn(Optional.of(entity));
        when(repo.findAnnouncementByTitle("Old")).thenReturn(Optional.of(entity));
        when(repo.findAll()).thenReturn(List.of(entity));
        when(repo.save(any(Announcement.class))).thenAnswer(call -> call.getArgument(0));
        assertThat(service.findAnnouncementByTitle("Old").title()).isEqualTo("Old");
        assertThat(service.findAnnouncements()).hasSize(1);
        service.update(AnnouncementDTO.builder().id(1).title("New").content("Updated").build());
        assertThat(entity.getTitle()).isEqualTo("New");
        assertThat(service.create(AnnouncementDTO.builder().title("News").content("Body").authorId(2).build()).authorId()).isEqualTo(2);
        assertThatThrownBy(() -> service.create(AnnouncementDTO.builder().title("").build())).isInstanceOf(RuntimeException.class);
        service.delete(1L);
        verify(repo).delete(entity);
    }

    @Test
    void scheduleCrudAndValidation() {
        ScheduleRepository repo = mock(ScheduleRepository.class);
        ScheduleService service = new ScheduleService(repo);
        ScheduleDTO dto = ScheduleDTO.builder().subjectId(1).groupId(2).teacherId(3).dayOfWeek("MONDAY").lessonNum(1).weekType(1).build();
        when(repo.save(any(Schedule.class))).thenAnswer(call -> call.getArgument(0));
        Schedule schedule = Schedule.builder().subject(Subject.builder().id(1).build()).group(Group.builder().id(2).build()).teacher(Teacher.builder().id(3).build()).build();
        when(repo.findById(1L)).thenReturn(Optional.of(schedule));
        when(repo.findScheduleById(1L)).thenReturn(Optional.of(schedule));
        when(repo.findAll()).thenReturn(List.of(schedule));
        assertThat(service.create(dto).groupId()).isEqualTo(2);
        service.update(1L, dto);
        assertThat(service.findSchedule(1L).dayOfWeek()).isEqualTo("MONDAY");
        assertThat(service.findSchedules()).hasSize(1);
        assertThatThrownBy(() -> service.create(ScheduleDTO.builder().build())).isInstanceOf(RuntimeException.class);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void teacherCrudAndRelations() {
        TeacherRepository repo = mock(TeacherRepository.class);
        TeacherService service = new TeacherService(repo);
        Teacher teacher = Teacher.builder().id(3).fullName("Teacher One").login("teacher").password(PasswordUtil.hashPassword("secret123"))
                .groups(List.of(Group.builder().id(4).name("A1").build())).subjects(List.of(Subject.builder().id(5).subjectName("Math").build())).build();
        when(repo.findById(3L)).thenReturn(Optional.of(teacher));
        when(repo.findByIdWithGroups(3L)).thenReturn(Optional.of(teacher));
        when(repo.findByIdWithSubjects(3L)).thenReturn(Optional.of(teacher));
        when(repo.findByLogin("teacher")).thenReturn(Optional.of(teacher));
        when(repo.findAll()).thenReturn(List.of(teacher));
        when(repo.save(any(Teacher.class))).thenAnswer(call -> call.getArgument(0));
        assertThat(service.findTeacher(3).groups()).hasSize(1);
        assertThat(service.findTeachers()).hasSize(1);
        assertThat(service.findGroupsByTeacher(3)).hasSize(1);
        assertThat(service.findSubjectsByTeacher(3)).hasSize(1);
        assertThat(service.create(TeacherDTO.builder().fullName("Teacher Two").login("other").password("secret123").build()).login()).isEqualTo("other");
        service.update("teacher", TeacherDTO.builder().password("newpass123").build());
        assertThat(PasswordUtil.checkPassword("newpass123", teacher.getPassword())).isTrue();
        service.delete("teacher");
        verify(repo).delete(teacher);
    }

    @Test
    void studentCrudHashesPasswordsAndCreatesGroup() {
        StudentRepository repo = mock(StudentRepository.class);
        GroupService groups = mock(GroupService.class);
        StudentService service = new StudentService(repo, groups);
        StudentDTO dto = StudentDTO.builder().fullName("Student One").login("student").password("secret123").groupName("A1").build();
        when(repo.save(any(Student.class))).thenAnswer(call -> call.getArgument(0));
        assertThat(service.create(dto).login()).isEqualTo("student");
        verify(groups).create(any(Group.class));
        Student student = Student.builder().id(2).login("student").password(PasswordUtil.hashPassword("secret123")).build();
        when(repo.findByLogin("student")).thenReturn(Optional.of(student));
        when(repo.findById(2L)).thenReturn(Optional.of(student));
        when(repo.findAll()).thenReturn(List.of(student));
        assertThat(service.findStudent(2).login()).isEqualTo("student");
        assertThat(service.findStudents()).hasSize(1);
        service.update("student", StudentDTO.builder().login("student").password("newpass123").build());
        assertThat(PasswordUtil.checkPassword("newpass123", student.getPassword())).isTrue();
        service.delete("student");
        verify(repo).delete(student);
    }
    @Test
    void homeworkFindMapsRelationsAndDeadline() {
        HomeworkRepository repo = mock(HomeworkRepository.class);
        HomeworkService service = new HomeworkService(repo, mock(HomeworkStatusRepository.class), mock(StudentRepository.class), mock(GroupRepository.class));
        Homework homework = Homework.builder().id(4).title("Task").description("Work").deadline(Instant.parse("2030-01-01T00:00:00Z"))
                .group(Group.builder().id(5).build()).teacher(Teacher.builder().id(6).build()).subject(Subject.builder().id(7).build()).build();
        when(repo.findHomeworkById(4L)).thenReturn(Optional.of(homework));
        assertThat(service.findHomework(4).groupId()).isEqualTo(5);
        assertThat(service.findHomework(4).deadline()).isEqualTo("2030-01-01T00:00:00Z");
    }

    @Test
    void statusMappingHandlesMissingTimestamp() {
        HomeworkStatusService service = new HomeworkStatusService(mock(HomeworkStatusRepository.class), mock(HomeworkRepository.class), mock(StudentRepository.class));
        HomeworkStatus status = HomeworkStatus.builder().homework(Homework.builder().id(10).build()).student(Student.builder().id(200).build()).build();
        assertThat(service.map(status).studentId()).isEqualTo(200);
        assertThat(service.map(status).markedAt()).isNull();
        assertThat(service.map(status).isDone()).isFalse();
    }

    @Test
    void scheduleQueriesByGroupName() {
        ScheduleRepository repo = mock(ScheduleRepository.class);
        ScheduleService service = new ScheduleService(repo);
        Schedule schedule = Schedule.builder().id(1).group(Group.builder().id(2).build()).subject(Subject.builder().id(3).build()).teacher(Teacher.builder().id(4).build()).dayOfWeek("MONDAY").lessonNum(1).weekType(1).build();
        when(repo.findScheduleByGroup_Name("A1")).thenReturn(List.of(schedule));
        assertThat(service.findScheduleByGroupName("A1")).singleElement().satisfies(dto -> assertThat(dto.groupId()).isEqualTo(2));
    }

    @Test
    void announcementFindMapsAuthor() {
        AnnouncementRepository repo = mock(AnnouncementRepository.class);
        AnnouncementService service = new AnnouncementService(repo);
        Announcement announcement = Announcement.builder().id(1).title("News").content("Hello").authorId(Teacher.builder().id(3).build()).build();
        when(repo.findById(1L)).thenReturn(Optional.of(announcement));
        assertThat(service.findAnnouncement(1).authorId()).isEqualTo(3);
    }
    @Test
    void passwordHashesAndChecks() {
        String hash = PasswordUtil.hashPassword("correct horse");
        assertThat(hash).isNotEqualTo("correct horse");
        assertThat(PasswordUtil.checkPassword("correct horse", hash)).isTrue();
        assertThat(PasswordUtil.checkPassword("wrong", hash)).isFalse();
    }

    @Test
    void subjectCreateUpdateAndFind() {
        SubjectRepository repo = mock(SubjectRepository.class);
        SubjectService service = new SubjectService(repo, mock(TeacherRepository.class));
        Subject entity = Subject.builder().id(8).subjectName("Math").build();
        when(repo.save(any(Subject.class))).thenAnswer(call -> call.getArgument(0));
        when(repo.findSubjectById(8L)).thenReturn(Optional.of(entity));
        assertThat(service.create(SubjectDTO.builder().id(8).subjectName("Math").build()).subjectName()).isEqualTo("Math");
        assertThat(service.update(SubjectDTO.builder().id(8).subjectName("Physics").build()).subjectName()).isEqualTo("Physics");
        assertThat(service.findSubject(8L).subjectName()).isEqualTo("Physics");
    }

    @Test
    void groupCreateAndAssignTeacher() {
        GroupRepository groups = mock(GroupRepository.class);
        TeacherRepository teachers = mock(TeacherRepository.class);
        GroupService service = new GroupService(groups, teachers);
        Group group = Group.builder().id(5).name("A1").build();
        Teacher teacher = Teacher.builder().id(9).build();
        when(groups.save(any(Group.class))).thenReturn(group);
        when(teachers.findById(9L)).thenReturn(Optional.of(teacher));
        when(groups.findAllById(List.of(5L))).thenReturn(List.of(group));
        assertThat(service.create(group).name()).isEqualTo("A1");
        service.assignGroupsToTeacher(9L, List.of(5L));
        assertThat(teacher.getGroups()).containsExactly(group);
        verify(teachers).save(teacher);
    }

    @Test
    void studentLoginChecksPassword() {
        StudentRepository repo = mock(StudentRepository.class);
        StudentService service = new StudentService(repo, mock(GroupService.class));
        Student student = Student.builder().id(2).login("student").password(PasswordUtil.hashPassword("secret")).groupName("A1").build();
        when(repo.findByLogin("student")).thenReturn(Optional.of(student));
        assertThat(service.logIn(StudentDTO.builder().login("student").password("secret").build()).id()).isEqualTo(2);
        assertThatThrownBy(() -> service.logIn(StudentDTO.builder().login("student").password("incorrect").build())).isInstanceOf(RuntimeException.class);
    }

    @Test
    void teacherLoginChecksPassword() {
        TeacherRepository repo = mock(TeacherRepository.class);
        TeacherService service = new TeacherService(repo);
        Teacher teacher = Teacher.builder().id(3).login("teacher").password(PasswordUtil.hashPassword("secret")).build();
        when(repo.findByLogin("teacher")).thenReturn(Optional.of(teacher));
        assertThat(service.logIn(TeacherDTO.builder().login("teacher").password("secret").build()).id()).isEqualTo(3);
        assertThatThrownBy(() -> service.logIn(TeacherDTO.builder().login("teacher").password("incorrect").build())).isInstanceOf(RuntimeException.class);
    }
}
