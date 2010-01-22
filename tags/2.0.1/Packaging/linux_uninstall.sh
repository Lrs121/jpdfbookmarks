#!/bin/bash

#
# linux_uninstall.sh
# 
# Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
# All rights reserved.
# 
# This file is part of JPdfBookmarks.
# 
# JPdfBookmarks is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# JPdfBookmarks is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
#

if [[ $(/usr/bin/id -u) -ne 0 ]]; then
    echo "To run this script you must be root!"
    exit
fi

DIR_IN_PATH=/usr/local/bin
INSTALL_PATH=/usr/local/lib

SCRIPT_DIR=$(cd $(dirname "$0"); pwd)
VERSION=$(cat ${SCRIPT_DIR}/VERSION)
NAME=jpdfbookmarks-${VERSION}
INSTALL_DIR=${INSTALL_PATH}/${NAME}

rm ${DIR_IN_PATH}/jpdfbookmarks_gui
rm ${DIR_IN_PATH}/jpdfbookmarks
rm -f -R ${INSTALL_DIR}

