import argparse
import subprocess
import sys

def generate_changelog(start_commit, end_commit, path_filter="src/", output_file="changelog.md"):
	try:
		git_format = "- `%h` %s *(%an)*"
		git_command = [
			"git", "log",
			f"{start_commit}..{end_commit}",
			f"--pretty=format:{git_format}",
			"--", *path_filter,
		]
		commits = subprocess.check_output(git_command, text=True).strip()
		if not commits:
			print(f"No commits found between {start_commit} and {end_commit}.")
			return
		with open(output_file, "w", encoding="utf-8") as f:
			f.write(f"# Changelog\n")
			f.write(commits)
			f.write("\n")
		print(f"Generated '{output_file}' with {len(commits.splitlines())} commits.")
	except Exception as e:
		print(f"An error occurred: {e}", file=sys.stderr)
		sys.exit(1)

if __name__ == "__main__":
	parser = argparse.ArgumentParser(description="Generate a changelog.md from git commit history.")
	parser.add_argument("start", help="The start commit hash/tag")
	parser.add_argument("--end", default="HEAD", help="The end commit hash/tag")
	parser.add_argument("--path", nargs="+", default=["src/"], help="Filter commits that change a specific path")
	parser.add_argument("--output", default="changelog.md", help="The output file name")
	args = parser.parse_args()
	generate_changelog(args.start, args.end, args.path, args.output)
