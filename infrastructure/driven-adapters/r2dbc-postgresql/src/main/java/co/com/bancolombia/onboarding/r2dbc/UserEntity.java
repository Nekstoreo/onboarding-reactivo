package co.com.bancolombia.onboarding.r2dbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity implements Persistable<String> {
    @Id
    private String id;
    private String email;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String avatar;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
