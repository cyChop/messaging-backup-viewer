/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keyboardplaying.messaging.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.keyboardplaying.messaging.ui.icon.IconSize;
import org.keyboardplaying.messaging.ui.icon.ImageLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * A component for the user to manage the application.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
@Component
public class ApplicationManager {

    @Autowired
    private ConfigurableApplicationContext context;

    private TrayIcon trayIcon;

    @PostConstruct
    public void loadManager() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            // FIXME make a window instead
        } else {
            makeTrayIcon();
        }
    }

    @PreDestroy
    public void destroy() {
        destroyTrayIcon();
    }

    private void makeTrayIcon() {
        ImageLoader imgLoader = new ImageLoader();

        Image icon = imgLoader.getImage("messaging", IconSize.W_16);
        trayIcon = new TrayIcon(icon);
        int width = trayIcon.getSize().width;
        if (width > 16) {
            icon = imgLoader.getImage("messaging", IconSize.W_32);
        }
        trayIcon.setImage(icon.getScaledInstance(width, -1, Image.SCALE_SMOOTH));

        // Add a menu
        PopupMenu popup = makeMenu();
        trayIcon.setPopupMenu(popup);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    private void destroyTrayIcon() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }

    private PopupMenu makeMenu() {
        // Create a pop-up menu components
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                context.close();
            }
        });

        // Add components to pop-up menu
        PopupMenu popup = new PopupMenu();
        popup.add(exitItem);
        return popup;
    }
}
