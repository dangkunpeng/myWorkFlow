package dang.kp.manager.biz.k12.dao;

import dang.kp.manager.biz.k12.pojo.K12Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface K12LogDao extends JpaRepository<K12Log, String> {

    boolean existsByClassId(String classId);

    List<K12Log> getByAttendedAndDateUpdateLikeAndStudentIdIn(Boolean attended, String dateUpdate, List<String> studentIds);
}
