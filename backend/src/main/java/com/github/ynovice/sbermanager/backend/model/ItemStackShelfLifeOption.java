package com.github.ynovice.sbermanager.backend.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "item_stacks_shelf_life_options")
@Getter
@Setter
public class ItemStackShelfLifeOption {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;
    private static final int SECONDS_IN_MONTH = SECONDS_IN_DAY * 30;
    private static final int SECONDS_IN_YEAR = SECONDS_IN_MONTH * 12;

    public ItemStackShelfLifeOption(String storageMode) {
        this(storageMode, null);
    }

    public ItemStackShelfLifeOption(String storageMode, String shelfLifePresentation) {
        this.storageMode = storageMode;
        setShelfLifePresentation(shelfLifePresentation);
    }

    public ItemStackShelfLifeOption() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storageMode;
    private String shelfLifePresentation;

    @Setter(AccessLevel.NONE)
    private Integer shelfLifeInSeconds;

    @Setter(AccessLevel.NONE)
    private ZonedDateTime startCountingFrom;

    @Setter(AccessLevel.NONE)
    private boolean isActive = false;

    @Setter(AccessLevel.NONE)
    private ZonedDateTime itemStackGoesBadAt;

    @Setter(AccessLevel.NONE)
    private boolean isPrimary = true;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private ItemStackShelfLifeOption primaryShelfLifeOption;

    @OneToOne(fetch = FetchType.EAGER)
    private ItemStack itemStack;

    public void activate(ZonedDateTime startCountingFrom) {
        this.startCountingFrom = startCountingFrom;
        isActive = true;
        itemStackGoesBadAt = calculateGoesBadAt();
        // todo даже если не указан уникальный срок годности, всё равно в пределах основного
    }

    public void activate() {
        isActive = true;
        itemStackGoesBadAt = calculateGoesBadAt();
    }

    public void deactivate() {
        isActive = false;
        itemStackGoesBadAt = null;
    }

    public void setShelfLifePresentation(String shelfLifePresentation) {

        this.shelfLifePresentation = shelfLifePresentation;

        shelfLifeInSeconds = shelfLifePresentationToSeconds(shelfLifePresentation);
        itemStackGoesBadAt = calculateGoesBadAt();
    }

    public void setPrimaryShelfLifeOption(ItemStackShelfLifeOption primaryShelfLifeOption) {
        this.primaryShelfLifeOption = primaryShelfLifeOption;
//        isPrimary = primaryShelfLifeOption == null;
        isPrimary = false;
        itemStackGoesBadAt = calculateGoesBadAt();
    }

    public String getGoesBadInPresentation() {

        Long secondsUntilGoesBad = getSecondsUntilGoesBad();

        if(secondsUntilGoesBad == null) return null;

        if(secondsUntilGoesBad < 0) return "испорчен";

        if(secondsUntilGoesBad < SECONDS_IN_MINUTE) return "несколько мнгновений";

        if(secondsUntilGoesBad < SECONDS_IN_HOUR) return secondsUntilGoesBad / SECONDS_IN_MINUTE + " мин";

        if(secondsUntilGoesBad < SECONDS_IN_HOUR * 2) return
            String.format("1 ч %d мин", (secondsUntilGoesBad - SECONDS_IN_HOUR) / SECONDS_IN_MINUTE);

        if(secondsUntilGoesBad < SECONDS_IN_DAY) return secondsUntilGoesBad / SECONDS_IN_HOUR + " ч";

        if(secondsUntilGoesBad < SECONDS_IN_DAY * 2) return
                String.format("1 д %d ч", (secondsUntilGoesBad - SECONDS_IN_DAY) / SECONDS_IN_HOUR);

        if(secondsUntilGoesBad < SECONDS_IN_MONTH) return secondsUntilGoesBad / SECONDS_IN_DAY + " д";

        if(secondsUntilGoesBad < SECONDS_IN_MONTH * 2) return
                String.format("1 мес %d д", (secondsUntilGoesBad - SECONDS_IN_MONTH) / SECONDS_IN_DAY);

        if(secondsUntilGoesBad < SECONDS_IN_YEAR) return secondsUntilGoesBad / SECONDS_IN_MONTH + " мес";

        if(secondsUntilGoesBad < SECONDS_IN_YEAR * 2) return
                String.format("1 г %d мес", (secondsUntilGoesBad - SECONDS_IN_YEAR) / SECONDS_IN_MONTH);

        return secondsUntilGoesBad / SECONDS_IN_YEAR + " г";
    }

    public Long getSecondsUntilGoesBad() {
        if(itemStackGoesBadAt == null) return null;
        return ChronoUnit.SECONDS.between(ZonedDateTime.now(), itemStackGoesBadAt);
    }


    private ZonedDateTime calculateGoesBadAt() {

        ZonedDateTime calculatedGoesBadAt = null;

        if(startCountingFrom != null && shelfLifeInSeconds != null) {
            calculatedGoesBadAt = startCountingFrom.plusSeconds(shelfLifeInSeconds);
        }

        if(primaryShelfLifeOption != null) {

            ZonedDateTime primaryGoesBadAt = primaryShelfLifeOption.getItemStackGoesBadAt();

            if(primaryGoesBadAt != null && calculatedGoesBadAt != null && primaryGoesBadAt.isBefore(calculatedGoesBadAt))
                return primaryGoesBadAt;
        }

        return calculatedGoesBadAt;
    }

    private Integer shelfLifePresentationToSeconds(String shelfLifePresentation) {

        if(shelfLifePresentation == null) return null;

        String[] tokens = shelfLifePresentation.split(" ");

        if(tokens.length != 2) return null;

        int value;

        try {
            value = (int) Double.parseDouble(tokens[0].replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }

        return switch (tokens[1]) {
            case "сут" -> value * SECONDS_IN_DAY;
            case "мес" -> value * SECONDS_IN_MONTH;
            case "г" -> value * SECONDS_IN_YEAR;
            default -> null;  // todo make sure doesn't break anything
        };
    }
}
