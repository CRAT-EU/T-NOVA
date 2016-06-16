# Cluster Monitor Tool

This tool provides real-time visualization of the cluster member roles for all
shards in the config datastore. It is useful for understanding cluster behavior
in when controllers are isolated, downed, or rebooted.

A file named `cluster.json` containing a list of the IP addresses of the
controllers is required. This resides in the same directory as `monitor.py`.
"user" and "pass" are not required for `monitor.py`, but they may be
needed for other apps in this folder. Because this configuration
information unique to your environment, it may be more convenient to
copy the contents of this folder out of git to prevent these settings
from being overwritten by updates.


The file should look like this:

```
    {
        "cluster": {
            "controllers": [
                "172.17.10.93",
                "172.17.10.94",
                "172.17.10.95"
            ],
            "user": "username",
            "pass": "password"
        }
    }
```

Usage: `monitor.py`
