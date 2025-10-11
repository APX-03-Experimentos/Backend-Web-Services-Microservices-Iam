package apx.inc.iam_services.iam.domain.model.queries;

public record GetUserByUserNameQuery (String userName){
    public GetUserByUserNameQuery {
        if (userName == null || userName.isBlank()){
            throw new IllegalArgumentException("User name must be a positive number");
        }
    }
}
