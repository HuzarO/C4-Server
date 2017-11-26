@echo off
title eXtreme Register Game Server Console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./../libs/*; net.sf.l2j.gsregistering.GameServerRegister
@pause