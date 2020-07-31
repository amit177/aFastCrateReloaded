# aFastCrateReloaded

aFastCrateReloaded is a [CrateReloaded](https://www.spigotmc.org/resources/pro-crate-reloaded-mystery-crate-1-8-1-16-x.3663/) addon to open received keys from crates.

## Installation

1. Download the plugin and put it inside the 'plugins' directory
2. Restart the server to generate the config
3. Update `key-name-format` and `key-material` in `config.yml`
4. Restart the server

## Commands

The primary command is `/afastcratereloaded` and has the following aliases:

- `/afastcrates`
- `/fastcrates`
- `/afc`

The command requires the permission specified in `recursive-permission` inside `config.yml` to be able to access it and has the following subcmds:

### Toggle

Allows the player to choose whether to open keys received from crates or not.

### Skip

Lets the player skip specific received keys from opening.

For example, `/afc skip rank` will make the plugin skip opening the `rank crate` for the player executing it.

### List

Lists the crates the player has in their skip list


## Configuration

```yaml
# Plugin by amit177
# Formatting options for crate names:
# %crate% - first letter uppercase, rest lowercase
# %crate_lower% - full lowercase crate name
# %crate_upper% - full uppercase crate name

recursive-permission: afastcratereloaded.recursive
key-material: TRIPWIRE_HOOK
key-name-format: ' KEY'
show-help-credits: true
lang:
  prefix: '&8[&bCrates&8]'
  no-permission: '%prefix% &cYou do not have access to that command.'
  open-message:
    full: '%prefix% &7You''ve opened %result% &7crates.'
    crate-format: '&e%amount%x &c%crate_upper%,'
  commands:
    header: '&7Command List:'
    unknown: '%prefix% &cUnknown command.'
    toggle:
      help: '&e/%cmd% toggle &8- &7Toggle fast crate opening'
      enabled: '%prefix% &aFast crate opening is now enabled!'
      disabled: '%prefix% &cFast crate opening is now disabled!'
    skip:
      help: '&e/%cmd% skip <name> &8- &7Skip a crate when opening multiple keys'
      usage: '%prefix% &eUsage: /%cmd% skip <name>'
      invalid-crate: '%prefix% &cInvalid crate specified.'
      crate-added: '%prefix% &7The crate &e%crate_upper% &7has been added to your skip list!'
      crate-removed: '%prefix% &7The crate &e%crate_upper% &7has been removed from your
        skip list!'
    list:
      help: '&e/%cmd% list &8- &7List the crates in your skip list'
      empty: '%prefix% &7You have no crates in your skip list!'
      result: '%prefix% &eSkipped crates: %crates%.'

```

There are multiple placeholders for the crate names.

For example if the crate name is "rank":

- `%crate` - will output `Rank`
- `%crate_lower%` - will output `rank`
- `%crate_upper%` - will output `RANK`

## Metrics

The plugin uses [bStats](https://bstats.org/) to track usage.

<img src="https://bstats.org/signatures/bukkit/afastcratereloaded.svg"/>
