/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/navdrawer.dart';
import 'package:TaskTracker/page/pagedrawer.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/widget/widget.login.dart';
import 'package:TaskTracker/widget/widget.progresslist.dart';
import 'package:flutter/material.dart';


class PageHome extends StatefulWidget {
  final String title;

  PageHome({Key key, this.title}) : super(key: key);

  @override
  _PageHomeState createState() => _PageHomeState();
}

class _PageHomeState extends State<PageHome> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: NavDrawer(),
      appBar: AppBar(
        title: Text(widget.title),
        leading: PageDrawer.buildOpenDrawer(),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Padding(
                  padding: EdgeInsets.only(top: 20.0, bottom: 30),
                  child: Text(
                    Translator.text('PageHome', 'Welcome to Task Tracker'),
                    style: Theme.of(context).textTheme.headline6,
                  ),
                ),
                Padding(
                  padding: EdgeInsets.only(top: 10.0, bottom: 30),
                  child: Text(
                    Translator.text('PageHome', 'Track and report your project activities.'),
                  ),
                ),
                Visibility(
                  visible: (Config.authStatus.authenticated == false),
                  child: WidgetLogin(),
                ),
                Visibility(
                  visible: (Config.authStatus.authenticated == true),
                  child: Center(
                    child:
                      Column(
                        children: [
                          const SizedBox(height: 30),
                          Container(
                            constraints: BoxConstraints(maxWidth: Config.DEFAULT_PANEL_WIDTH),
                            child: WidgetProgressList(title: ""),
                          )
                        ],
                      ),
                    )
                  ),
               ],
            ),
          ),
        ],
      ),
    );
  }
}
