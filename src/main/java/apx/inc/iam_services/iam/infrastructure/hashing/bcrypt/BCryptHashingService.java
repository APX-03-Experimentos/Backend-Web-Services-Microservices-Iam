package apx.inc.iam_services.iam.infrastructure.hashing.bcrypt;

import apx.inc.iam_services.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {




}
