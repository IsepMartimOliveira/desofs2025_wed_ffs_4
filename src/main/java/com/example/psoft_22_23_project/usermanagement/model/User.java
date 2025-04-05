/*
 * Copyright (c) 2022-2022 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.psoft_22_23_project.usermanagement.model;

import com.example.psoft_22_23_project.devicemanagement.model.DeviceImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Based on https://github.com/Yoh0xFF/java-spring-security-example
 *
 */
@Entity
@Table(name = "app_user")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	// database primary key
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private Long id;

	// optimistic lock concurrency control
	@Version
	private Long version;


	@Setter
	@Getter
	private boolean enabled = true;

	@Column(unique = true, updatable = false, nullable = false)
	@Email
	@Getter
	@NotNull
	@NotBlank
	private String username;

	@Column(nullable = false)
	@Getter
	@NotNull
	@NotBlank
	private String password;

	@Getter
	@Setter
	private String location;

	@OneToOne(fetch = FetchType.EAGER)
	private UserImage userImage;

	public void setUserImage(UserImage userImage) {
		this.userImage = userImage;
	}

	@ElementCollection
	@Getter
	private final Set<Role> authorities = new HashSet<>();

	protected User() {
	}

	public User(final String username, final String password) {
		this.username = username;
		setPassword(password);
	}


	public static User newUser(final String username, final String password) {
		final var u = new User(username, password);
		return u;
	}


	public static User newUser(final String username, final String password, final String role) {
		final var u = new User(username, password);
		u.addAuthority(new Role(role));
		return u;
	}

	public void setPassword(final String password) {
		this.password = Objects.requireNonNull(password);
	}

	public void addAuthority(final Role r) {
		authorities.add(r);
	}

	@Override
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}
}
