#!/usr/bin/env python3
"""Fingerprint each built jar so releases only publish versions whose content changed.

Usage: fingerprint.py <mod version> <out.json>
Writes {"<mc>-<loader>": "<sha256>"} for every versions/*/build/libs jar.
The mod version is masked out, so bumping it alone doesn't change a fingerprint.
"""
import hashlib
import json
import pathlib
import sys
import zipfile

# Text files that carry the mod version
VERSIONED = {"fabric.mod.json", "META-INF/neoforge.mods.toml"}
# Build tool versions only; a Gradle/Loom bump shouldn't republish everything
SKIPPED = {"META-INF/MANIFEST.MF"}


def fingerprint(jar: pathlib.Path, version: str) -> str:
	digest = hashlib.sha256()
	with zipfile.ZipFile(jar) as zf:
		for name in sorted(zf.namelist()):
			if name in SKIPPED:
				continue
			data = zf.read(name)
			if name in VERSIONED:
				data = data.replace(version.encode(), b"@VERSION@")
			digest.update(name.encode() + b"\0" + hashlib.sha256(data).digest())
	return digest.hexdigest()


def main() -> None:
	version, out = sys.argv[1], sys.argv[2]
	result = {}
	for jar in sorted(pathlib.Path("versions").glob("*/build/libs/*.jar")):
		if jar.name.endswith("-sources.jar"):
			continue
		result[jar.parts[1]] = fingerprint(jar, version)
	pathlib.Path(out).write_text(json.dumps(result, indent="\t", sort_keys=True) + "\n")


if __name__ == "__main__":
	main()
