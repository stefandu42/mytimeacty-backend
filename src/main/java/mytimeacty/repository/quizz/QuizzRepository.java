package mytimeacty.repository.quizz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mytimeacty.model.quizzes.Quizz;
import mytimeacty.model.users.User;

@Repository
public interface QuizzRepository extends JpaRepository<Quizz, Integer>, JpaSpecificationExecutor<Quizz> {
	long countByCreatorAndIsVisible(User creator, Boolean isVisible);
}
