package com.project.schoolmanagment.contactmessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
//TODO learn about serialization and de-serialization
public class ContactMessage implements Serializable {

	//TODO check all generation types and strategies
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//better to give a naming contactMessageId, contactMessageName, contactMessageSubject
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private String email;

	@NotNull
	private String subject;

	@NotNull
	private String message;

	//2025-06-05
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "US")
	private LocalDateTime dateTime;

}
