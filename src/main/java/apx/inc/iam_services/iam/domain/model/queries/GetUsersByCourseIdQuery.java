package apx.inc.iam_services.iam.domain.model.queries;

public record GetUsersByCourseIdQuery(Long courseId) {
    public GetUsersByCourseIdQuery{
        if (courseId==null||courseId<=0){
            throw new IllegalArgumentException("Course ID must be a positive number");
        }
    }
}
