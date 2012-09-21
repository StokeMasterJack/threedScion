#!/usr/bin/python

import os
import sys
import shutil

rootdir = "/www/threed/configurator-content-v2/scion"

for root, subFolders, files in os.walk(rootdir):

    if os.path.dirname(root).endswith('/.git/.gen/jpgs'):
        fileName = os.path.basename(root)
        if fileName != 'wStd' and fileName != 'wStdP':
            shutil.rmtree(root)