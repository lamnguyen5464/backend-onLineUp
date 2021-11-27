package com.server.onlineup.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Room")
public class RoomEntity {
    @Id
    private String id = UUID.randomUUID().toString();

    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,
            orphanRemoval = true)
    Set<RoomUserEntity> userList = new HashSet<RoomUserEntity>();

    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,
            orphanRemoval = true)
    Set<RoomAdminEntity> adminList = new HashSet<RoomAdminEntity>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private Date endDate;

    private long processUnit;

    private String address;

    private int timeZoneOffset;

    private int maxQueuer;


    public void addUser(ProfileEntity user) {
        RoomUserEntity roomUserEntity = new RoomUserEntity(user, this);
        userList.add(roomUserEntity);
    }

    public void addHost(ProfileEntity admin) {
        RoomAdminEntity roomAdminEntity = new RoomAdminEntity(admin, this);
        roomAdminEntity.changeRole();
        adminList.add(roomAdminEntity);
    }

    public void addCoHost(ProfileEntity admin) {
        RoomAdminEntity roomAdminEntity = new RoomAdminEntity(admin, this);
        roomAdminEntity.changeRole();
        adminList.add(roomAdminEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!(o instanceof RoomEntity))
            return false;
        RoomEntity post = (RoomEntity) o;
        return Objects.equals(id, post.getId()) && Objects.equals(userList, post.userList)
                && Objects.equals(adminList, post.adminList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getter & Setter
    public String getId() {
        return this.id;
    }

    public Set<RoomAdminEntity> getAdminList() {
        return this.adminList;
    }
}