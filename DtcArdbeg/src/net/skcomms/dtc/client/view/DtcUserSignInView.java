package net.skcomms.dtc.client.view;

import net.skcomms.dtc.client.controller.DtcConfigController;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DtcUserSignInView {
  public void initialize() {
    Button loginButton = new Button("login");
    RootPanel.get("loginContainer").add(loginButton);

    loginButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        final DialogBox loginDialog = new DialogBox();
        final TextBox usernameText = new TextBox();
        Button okButton = new Button("Ok");
        Button closeButton = new Button("Close");

        VerticalPanel loginPanel = new VerticalPanel();
        loginPanel.add(usernameText);
        loginPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonPanel.add(okButton);
        buttonPanel.add(closeButton);
        loginPanel.add(buttonPanel);

        loginDialog.setText("Login");
        loginDialog.add(loginPanel);
        loginDialog.center();
        usernameText.setFocus(true);

        usernameText.addKeyDownHandler(new KeyDownHandler() {

          @Override
          public void onKeyDown(KeyDownEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
              loginDialog.hide();
              RootPanel.get("usernameContainer").clear();
              RootPanel.get("usernameContainer").add(new HTML(usernameText.getValue()));
              DtcConfigController.getInstance().setUsername(usernameText.getValue());
            }
          }
        });

        okButton.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            loginDialog.hide();
            RootPanel.get("usernameContainer").clear();
            RootPanel.get("usernameContainer").add(new HTML(usernameText.getValue()));
            DtcConfigController.getInstance().setUsername(usernameText.getValue());
          }
        });

        closeButton.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            loginDialog.hide();
          }
        });
      }
    });
  }
}
