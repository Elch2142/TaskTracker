/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/page/pagedrawer.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.progresslist.dart';
import 'package:flutter/material.dart';

class PageViewProgress extends StatefulWidget {
  final String title;

  PageViewProgress({Key key, this.title}) : super(key: key);

  @override
  _PageViewProgressState createState() => _PageViewProgressState();
}

class _PageViewProgressState extends State<PageViewProgress> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading: PageDrawer.buildNavigateBack(),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: Center(
        child: ListView(
          shrinkWrap: true,
          children: [
            Center(
              child: WidgetProgressList(),
            ),
          ],
        ),
      ),
    );
  }
}
