@echo off
title eXtreme Account Manager Console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./../libs/*; net.sf.l2j.accountmanager.SQLAccountManager
@pause
