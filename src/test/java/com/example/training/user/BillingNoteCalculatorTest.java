package com.example.training.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BillingNoteCalculatorTest {

    private BillingNoteCalculator calculator;
    private SimpleDateFormat sdf;

    @BeforeEach
    void setUp() {
        calculator = new BillingNoteCalculator();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Test
    void shouldReturnTrueWhenUserHasNoCustomerNotes() {
        //given
        UserWS user = new UserWS();
        user.setCustomerNotes(null);

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenUserHasEmptyCustomerNotes() {
        //given
        UserWS user = new UserWS();
        user.setCustomerNotes(new CustomerNoteWS[0]);

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserHasRecentCancelledOnRequestNote() {
        //given
        UserWS user = new UserWS();
        CustomerNoteWS note = new CustomerNoteWS();
        note.setNoteTitle("Cancelled on Request");
        note.setCreationTime(new Date()); // Today
        user.setCustomerNotes(new CustomerNoteWS[]{note});

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserHasCancelledOnRequestNoteFromYesterday() {
        //given
        UserWS user = new UserWS();
        CustomerNoteWS note = new CustomerNoteWS();
        note.setNoteTitle("Cancelled on Request");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        note.setCreationTime(calendar.getTime()); // Yesterday

        user.setCustomerNotes(new CustomerNoteWS[]{note});

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenUserHasCancelledOnRequestNoteOlderThanYesterday() {
        //given
        UserWS user = new UserWS();
        CustomerNoteWS note = new CustomerNoteWS();
        note.setNoteTitle("Cancelled on Request");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2); // Two days ago
        note.setCreationTime(calendar.getTime());

        user.setCustomerNotes(new CustomerNoteWS[]{note});

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenUserHasRecentNoteWithDifferentTitle() {
        //given
        UserWS user = new UserWS();
        CustomerNoteWS note = new CustomerNoteWS();
        note.setNoteTitle("Some Other Title");
        note.setCreationTime(new Date()); // Today
        user.setCustomerNotes(new CustomerNoteWS[]{note});

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserHasMultipleNotesIncludingRecentCancelledOnRequest() {
        //given
        UserWS user = new UserWS();

        CustomerNoteWS oldNote = new CustomerNoteWS();
        oldNote.setNoteTitle("Some Other Title");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -5);
        oldNote.setCreationTime(calendar.getTime());

        CustomerNoteWS cancelledNote = new CustomerNoteWS();
        cancelledNote.setNoteTitle("Cancelled on Request");
        cancelledNote.setCreationTime(new Date()); // Today

        user.setCustomerNotes(new CustomerNoteWS[]{oldNote, cancelledNote});

        //when
        boolean result = calculator.hasBillingNote(user);

        //then
        assertThat(result).isFalse();
    }
}