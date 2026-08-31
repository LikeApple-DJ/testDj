"""支持 `python -m todoapp` 直接运行。"""

from todoapp.cli import main

raise SystemExit(main())
