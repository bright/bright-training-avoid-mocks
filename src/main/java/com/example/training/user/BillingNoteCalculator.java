package com.example.training.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BillingNoteCalculator {
  boolean hasBillingNote(UserWS user) {
    CustomerNoteWS customerNotes[] = user.getCustomerNotes();
    boolean addJbillingNote = true;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Calendar calendar = Calendar.getInstance();
    String today = sdf.format(calendar.getTime());

    calendar.add(Calendar.DATE, -1);
    String yesterday = sdf.format(calendar.getTime());

    if (customerNotes != null && customerNotes.length > 0) {
      for (CustomerNoteWS note : customerNotes) {
        String noteCreatedTime = sdf.format(note.getCreationTime());
        String title = note.getNoteTitle();

        if ((noteCreatedTime.equals(today) || noteCreatedTime.equals(yesterday)) && title.equals("Cancelled on Request")) {
          addJbillingNote = false;
          break;
        }
      }
    }
    return addJbillingNote;
  }
}
