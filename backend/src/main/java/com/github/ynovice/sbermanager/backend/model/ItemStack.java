package com.github.ynovice.sbermanager.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "item_stacks")
@Getter
@Setter
public class ItemStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private ZonedDateTime placedAt;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private ItemStackShelfLifeOption primaryShelfLifeOption;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private ItemStackShelfLifeOption afterOpeningShelfLifeOption;

    private String smSku;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    @Column(length = 10)
    private String amountPerPack;

    @Column(nullable = false)
    private Integer count;

    @Column(length = 800)
    private String imageUrl;

    public Long getOwnerId() {
        return owner == null ? null : owner.getId();
    }

    /**
     * @return true     if item stack is opened
     *         false    if item stack isn't opened, but can be opened
     *         null     if item stack is not opened and cannot be opened
     */
    public Boolean isOpened() {

        boolean afterOpeningOptionExists = afterOpeningShelfLifeOption != null;
        boolean afterOpeningOptionIsActive = afterOpeningOptionExists && afterOpeningShelfLifeOption.isActive();

        if(afterOpeningOptionIsActive) return true;
        if(afterOpeningOptionExists) return false;

        return null;
    }

    public void open() {

        if(isOpened() == null || isOpened()) return;

        if(primaryShelfLifeOption != null) primaryShelfLifeOption.deactivate();
        afterOpeningShelfLifeOption.activate(ZonedDateTime.now());
    }

    public void close() {

        if(isOpened() == null || !isOpened()) return;

        if(primaryShelfLifeOption != null) primaryShelfLifeOption.activate();
        afterOpeningShelfLifeOption.deactivate();
    }

    public String getGoesBadInPresentation() {

        if(afterOpeningShelfLifeOption != null && afterOpeningShelfLifeOption.isActive()) {
            return afterOpeningShelfLifeOption.getGoesBadInPresentation();
        } else if (primaryShelfLifeOption != null && primaryShelfLifeOption.isActive()) {
            return primaryShelfLifeOption.getGoesBadInPresentation();
        } else {
            return null;
        }
    }

    public Long getSecondsUntilGoesBad() {

        if(afterOpeningShelfLifeOption != null && afterOpeningShelfLifeOption.isActive()) {
            return afterOpeningShelfLifeOption.getSecondsUntilGoesBad();
        } else if (primaryShelfLifeOption != null && primaryShelfLifeOption.isActive()) {
            return primaryShelfLifeOption.getSecondsUntilGoesBad();
        } else {
            return null;
        }
    }
}
