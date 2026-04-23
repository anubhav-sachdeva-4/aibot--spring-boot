package com.sachdeva.Aibot.Model;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name="bot")
public class Bot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long botId;
    private String name;
    private String personalDescription;

}
