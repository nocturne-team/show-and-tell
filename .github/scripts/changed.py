#!/usr/bin/env python3
"""Print the Stonecutter projects whose fingerprint differs from the previous release.

Usage: changed.py <current.json> [<previous.json>]
With no previous file (first release, or it predates fingerprints) every project counts as changed.
"""
import json
import pathlib
import sys

current = json.loads(pathlib.Path(sys.argv[1]).read_text())
previous = {}
if len(sys.argv) > 2 and pathlib.Path(sys.argv[2]).is_file():
	previous = json.loads(pathlib.Path(sys.argv[2]).read_text())

print(" ".join(node for node, digest in sorted(current.items()) if previous.get(node) != digest))
